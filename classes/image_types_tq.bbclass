inherit image_types


### SDCARD image ###

# Set alignment to 4MB [in KiB]
IMAGE_ROOTFS_ALIGNMENT = "4096"

# Set boot partition size to 32MB [in KiB]
IMAGE_BOOT_PARTITION_SIZE = "32768"

IMAGE_DEPENDS_sdcard = " \
	parted-native:do_populate_sysroot \
	mtools-native:do_populate_sysroot \
	dosfstools-native:do_populate_sysroot \
	virtual/kernel:do_deploy \
	u-boot:do_deploy \
	fm-ucode:do_deploy \
"

SDCARD = "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.sdcard"
UBOOT_SUFFIX_SDCARD ?= "bin-sdcard"
UBOOT_SUFFIX_NOR ?= "bin"

sector_add_kiB () {
	sector_size=512
	echo $(expr $1 \+ \( 1024 \* $2 \) / $sector_size)
}
sector_next_primary () {
    parted -s -m ${SDCARD} unit s print free | grep free | cut -d: -f2 | tail -n1 | tr -dc '0-9'
}

IMAGE_CMD_sdcard () {
	if [ -z "${SDCARD_ROOTFS}" ]; then
		bberror "SDCARD_ROOTFS is undefined. To use sdcard image it needs to be defined."
		exit 1
	fi
	if [ ! -e "${SDCARD_ROOTFS}" ]; then
		bberror "${SDCARD_ROOTFS} does not exist! Aborting."
		exit 1
	fi

	PARTED_OPTIMIZE=optimal

	# Calculate total SD card image size
	SDCARD_ROOTFS_SIZE=$(stat -c '%s' ${SDCARD_ROOTFS})
	SDCARD_ROOTFS_SIZE=$(expr \( $SDCARD_ROOTFS_SIZE \+ 1023 \) / 1024 ) # Round up to full kilobytes
	SDCARD_SIZE=$(expr \
		${IMAGE_ROOTFS_ALIGNMENT} \+ \
		${IMAGE_BOOT_PARTITION_SIZE} \+ \
		${SDCARD_ROOTFS_SIZE} \+ \
		${IMAGE_ROOTFS_ALIGNMENT} \
	)

	# Initialize a sparse file
	dd if=/dev/zero of=${SDCARD} bs=1024 count=0 seek=${SDCARD_SIZE}

	# Create partition table
	parted -s ${SDCARD} mklabel msdos

	# Create boot partition
	start=$(sector_add_kiB 0 ${IMAGE_ROOTFS_ALIGNMENT})
	end=$(sector_add_kiB $start ${IMAGE_BOOT_PARTITION_SIZE})
	parted -s -a $PARTED_OPTIMIZE ${SDCARD} unit s mkpart primary $start $end
	PART_BOOT_OFFSET=$start

	# Create partition for System image
	start=$(sector_next_primary)
	end=$(sector_add_kiB $start ${SDCARD_ROOTFS_SIZE})
	parted -s -a $PARTED_OPTIMIZE ${SDCARD} unit s mkpart primary $start $end
	PART_ROOTFS_OFFSET=$start

	parted ${SDCARD} unit MiB print free

	# Burn bootloader with RCW (Fixed offset: 0x1000)
	dd if=${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.${UBOOT_SUFFIX_SDCARD} of=${SDCARD} conv=notrunc seek=8 bs=512

	# Burn FMAN (Fixed offset: 0x104000)
	dd if=${DEPLOY_DIR_IMAGE}/${FSL_FMAN_UCODE} of=${SDCARD} conv=notrunc seek=2080 bs=512

	# Build boot partition
	BOOT_IMG=${WORKDIR}/boot.vfat
	dd if=/dev/zero of=$BOOT_IMG bs=1024 count=0 seek=${IMAGE_BOOT_PARTITION_SIZE}
	mkfs.vfat -n "Boot ${MACHINE}" $BOOT_IMG
	mmd -i $BOOT_IMG ::/"boot"
	mcopy -/ -i $BOOT_IMG ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin ::/"boot"/${KERNEL_IMAGETYPE}-${MACHINE}.bin

	# Copy device tree file
	if test -n "${KERNEL_DEVICETREE}"; then
		for DTS_FILE in ${KERNEL_DEVICETREE}; do
			DTS_BASE_NAME=$(basename ${DTS_FILE} | awk -F "." '{print $1}')
			if [ -e "${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb" ]; then
				kernel_bin="$(readlink ${KERNEL_IMAGETYPE}-${MACHINE}.bin)"
				kernel_bin_for_dtb="$(readlink ${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb \
					| sed "s,$DTS_BASE_NAME,${MACHINE},g;s,\.dtb$,.bin,g" \
				)"
				if [ $kernel_bin = $kernel_bin_for_dtb ]; then
					mcopy -i $BOOT_IMG -s ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb ::/"boot"/${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb
				fi
			fi
		done
	fi

	# Burn boot image
	dd if=$BOOT_IMG of=${SDCARD} conv=notrunc seek=$PART_BOOT_OFFSET bs=512
	rm $BOOT_IMG
	
	# Burn Root FS
	dd if=${SDCARD_ROOTFS} of=${SDCARD} conv=notrunc seek=$PART_ROOTFS_OFFSET bs=512
}

# The sdcard requires the rootfs filesystem to be built before using
# it so we must make this dependency explicit.
IMAGE_TYPEDEP_sdcard = "${@d.getVar('SDCARD_ROOTFS', 1).split('.')[-1]}"


### NOR image ###
#
# NOR partitions:
# Start        Size                  Description
# 0x00000000   0x00020000 (64KiB)    Reset Configuration Word
# 0x00020000   0x00800000 (8MiB)     Linux kernel image
# 0x00820000   0x00020000 (64KiB)    Linux device tree blob
# 0x00840000   0x04000000 (64MiB)    RootFS
# 0x04840000   0x03600000 (54MiB)    Spare partition
# 0x07f00000   0x00020000 (64KiB)    Frame manager microcode and QUICC engine microcode
# 0x07f20000   0x00020000 (64KiB)    U-Boot environment
# 0x07f40000   0x000c0000 (768Kib)   U-Boot image

NOR_IMAGE = "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.nor"

IMAGE_DEPENDS_nor = " \
	virtual/kernel:do_deploy \
	u-boot:do_deploy \
	fm-ucode:do_deploy \
"

nor_burn_file () {
	# Hex to dec
	OFFSET=$(printf "%d" $2)
	MAX_FILE_SIZE=$(printf "%d" $3)

	FILE_SIZE=$(stat -c '%s' "$1")
	if [ $FILE_SIZE -gt $MAX_FILE_SIZE ]; then
		bberror "File $1 does not fit into partition!"
		exit 1
	fi

	OFFSET_KB=$(expr $OFFSET / 1024 ) || true

	dd if="$1" of=${NOR_IMAGE}  conv=notrunc bs=1024 seek=$OFFSET_KB
}
IMAGE_CMD_nor () {
	NOR_IMAGE_SIZE="131072" # 128MiB [in KiB]

	if [ -z "${NOR_ROOTFS}" ]; then
		bberror "NOR_ROOTFS is not set! Aborting."
		exit 1
	fi
	if [ ! -e "${NOR_ROOTFS}" ]; then
		bberror "${NOR_ROOTFS} does not exist! Aborting."
		exit 1
	fi

	# Initialize sparse file
	dd if=/dev/zero of=${NOR_IMAGE} bs=1024 count=0 seek=${NOR_IMAGE_SIZE}

	# Burn RCW
	nor_burn_file \
		"${DEPLOY_DIR_IMAGE}/fsl_rcw-nor-${FSL_RCW}.bin" \
		0x00000000 0x00020000

	# Burn Linux kernel image
	nor_burn_file \
		"${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin" \
		0x00020000 0x00800000

	# Burn Linux device tree blob
	if test -n "${KERNEL_DEVICETREE}"; then
		for DTS_FILE in ${KERNEL_DEVICETREE}; do
			DTS_BASE_NAME=$(basename ${DTS_FILE} | awk -F "." '{print $1}')
			if [ -e "${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb" ]; then
				kernel_bin="$(readlink ${KERNEL_IMAGETYPE}-${MACHINE}.bin)"
				kernel_bin_for_dtb="$(readlink ${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb \
					| sed "s,$DTS_BASE_NAME,${MACHINE},g;s,\.dtb$,.bin,g" \
				)"
				if [ $kernel_bin = $kernel_bin_for_dtb ]; then
					nor_burn_file \
						"${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb" \
						0x00820000 0x00020000
				fi
			fi
		done
	fi

	# Burn RootFS
	nor_burn_file \
		"${NOR_ROOTFS}" \
		0x00840000 0x04000000

	# Burn Frame manager microcode
	# TODO: QUICC engine microcode
	nor_burn_file \
		"${DEPLOY_DIR_IMAGE}/${FSL_FMAN_UCODE}" \
		0x07f00000 0x00020000

	# Burn U-Boot image
	nor_burn_file \
		"${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.${UBOOT_SUFFIX_NOR}" \
		0x07f40000 0x000c0000
}
IMAGE_TYPEDEP_nor = "${@d.getVar('NOR_ROOTFS', 1).split('.')[-1]}"
