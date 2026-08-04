inherit core-image

LICENSE = "CLOSED"

IMAGE_FSTYPES = "wic.bz2 wic.bmap"

IMAGE_INSTALL:append = "    esc-driver \
                            esc-sysfs \
                            pni-tcm3-driver \
                            pni-tcm3-sysfs \
                            i2c-tools \
"