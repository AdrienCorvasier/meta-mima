SUMMARY = "Userspace ASPEP/MCP driver, CLI tools and sysfs-relay daemon for the B-G431B-ESC1"
DESCRIPTION = "libesc_driver implements the ASPEP framing/handshake and Motor Control Protocol \
(MCP) used by the mima-esc firmware over its serial link, plus esc_cli (inspection/manual \
control), spin_test (bidirectional spin smoke test) and escd, which relays /dev/esc0 requests \
from the esc-sysfs kernel module (see that recipe) to libesc_driver."
HOMEPAGE = "https://github.com/AdrienCorvasier/mima-b-g431-esc1-driver"

# No LICENSE file in the repository yet; adjust to the project's actual
# license (and add a matching LIC_FILES_CHKSUM) once one is added.
LICENSE = "CLOSED"

SRC_URI = "git://github.com/AdrienCorvasier/mima-b-g431-esc1-driver.git;protocol=https;branch=master \
           file://escd.service \
           file://escd.default \
           "
# Pin to a real commit/tag before using this recipe outside of active development.
SRCREV = "${AUTOREV}"

inherit cmake systemd

# Tests pull in GoogleTest via FetchContent and are irrelevant on-target.
EXTRA_OECMAKE = " \
    -DESC_DRIVER_BUILD_TESTS=OFF \
    -DESC_DRIVER_BUILD_CLI=ON \
    "

SYSTEMD_SERVICE:${PN} = "escd.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/escd.service ${D}${systemd_system_unitdir}/escd.service

    install -d ${D}${sysconfdir}/default
    install -m 0644 ${UNPACKDIR}/escd.default ${D}${sysconfdir}/default/escd
}

FILES:${PN} += "${systemd_system_unitdir}/escd.service ${sysconfdir}/default/escd"
CONFFILES:${PN} += "${sysconfdir}/default/escd"

# esc-sysfs creates /dev/esc0; escd waits on it (After=dev-esc0.device) but
# doesn't hard-depend on the kernel module being present at install time.
RRECOMMENDS:${PN} += "esc-sysfs"
