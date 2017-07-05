SUMMARY = "T1040 L2 Switch"
DESCRIPTION = "Control application sample, headers and library"

SRC_URI = "file://l2switch-utils_v${PV}.zip"

LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "\
	file://Freescale_Software_License.txt;md5=704b1b597270faff2faf4fafb308c947 \
	file://COPYING.libvtss_api;md5=7018bb7bacb48a72a2acbf999cf7d25a \
	file://include/COPYING;md5=7018bb7bacb48a72a2acbf999cf7d25a \
"

RCONFLICTS_${PN} = "smbstax"

inherit update-rc.d
INITSCRIPT_NAME = "l2switch"
INITSCRIPT_PARAMS = "defaults 10 85"

S = "${WORKDIR}/l2switch-utils_v${PV}"

PACKAGES =+ "libvtss_api libvtss_api-dev"

FILES_${PN} = " \
	${bindir}/l2sw_bin \
	${bindir}/l2switch-cfg \
	/etc/init.d/l2switch \
	/etc/sysconfig/l2switch \
"
FILES_libvtss_api = "${libdir}/*.so.*"
FILES_libvtss_api-dev = " \
	${libdir}/*.so \
	${includedir}/* \
"

RDEPENDS_${PN} = "libvtss_api procps"

L2S_BINDIR_e5500 = "${S}/ppce5500"
L2S_BINDIR_e5500-64b = "${S}/ppc64e5500"

do_install() {
	if [ -z "${L2S_BINDIR}" ]; then
		bbfatal "Processor is not supported!"
		exit 1
	fi

	install -d ${D}${bindir}
	install -m 755 ${L2S_BINDIR}/l2sw_bin ${D}${bindir}
	install -m 755 scripts/l2switch-cfg ${D}${bindir}

	install -d ${D}/etc/init.d
	install -m 755 scripts/etc/init.d/l2switch ${D}/etc/init.d

	install -d ${D}/etc/sysconfig
	install -m 644 scripts/etc/sysconfig/l2switch ${D}/etc/sysconfig

	install -d ${D}${libdir}
	for f in ${L2S_BINDIR}/libvtss_api*; do
		if [ -L $f ]; then
			ln -sf $(readlink $f) ${D}${libdir}/$(basename $f)
		else
			install -m 644 $f ${D}${libdir}
		fi
	done

	install -d ${D}${includedir}
	find include -mindepth 1 -type d | cut -d/ -f2- | while read dir; do
		install -d ${D}${includedir}/$dir
	done
	find include -type f | cut -d/ -f2- | while read f; do
		install -m 644 include/$f ${D}${includedir}/$f
	done
}

INSANE_SKIP_${PN} = "already-stripped"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
COMPATIBLE_MACHINE = "(tqmt1040-stk|tqmt1040-64b-stk|tqmt1042-stk|tqmt1042-64b-stk)"
