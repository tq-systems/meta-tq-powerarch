# TQ BSP default settings

SDCARD_ROOTFS ?= "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.ext3"

NOR_ROOTFS ?= "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.jffs2"
JFFS2_ERASEBLOCK ?= "0x20000"

IMAGE_CLASSES += "image_types_tq"

IMAGE_TYPES += " nor sdcard"
