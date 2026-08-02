inherit core-image

LICENSE = "CLOSED"

IMAGE_FSTYPES = "wic.bz2 wic.bmap"

IMAGE_INSTALL:append = " esc-driver esc-sysfs"