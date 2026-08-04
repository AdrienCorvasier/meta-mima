LICENSE = "CLOSED"

SRC_URI = "git://github.com/AdrienCorvasier/mima-pni-tcm3-driver.git;protocol=https;branch=master \
           file://pni-tcm3d.service \
           file://pni-tcm3d.default \
           "
# Pin to a real commit/tag before using this recipe outside of active development.
SRCREV = "${AUTOREV}"

inherit cmake systemd

EXTRA_OECMAKE = " \
    -DTCM3_DRIVER_BUILD_TESTS=OFF \
    -DTCM3_DRIVER_BUILD_CLI=ON \
    "

SYSTEMD_SERVICE:${PN} = "pni-tcm3d.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/pni-tcm3d.service ${D}${systemd_system_unitdir}/pni-tcm3d.service

    install -d ${D}${sysconfdir}/default
    install -m 0644 ${UNPACKDIR}/pni-tcm3d.default ${D}${sysconfdir}/default/pni-tcm3d
}

FILES:${PN} += "${systemd_system_unitdir}/pni-tcm3d.service ${sysconfdir}/default/pni-tcm3d"
CONFFILES:${PN} += "${sysconfdir}/default/pni-tcm3d"

# pni-tcm3-sysfs creates /dev/pni-tcm3-0; pni-tcm3d waits on it (After=dev-pni-tcm3-0.device) but
# doesn't hard-depend on the kernel module being present at install time.
RRECOMMENDS:${PN} += "pni-tcm3-sysfs"
