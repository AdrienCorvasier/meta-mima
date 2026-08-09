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
           file://escd@.service \
           file://escd-0.default \
           file://escd-1.default \
           file://escd-2.default \
           file://escd-3.default \
           file://escd-4.default \
           "
# Pin to a real commit/tag before using this recipe outside of active development.
SRCREV = "${AUTOREV}"

inherit cmake systemd

# Tests pull in GoogleTest via FetchContent and are irrelevant on-target.
EXTRA_OECMAKE = " \
    -DESC_DRIVER_BUILD_TESTS=OFF \
    -DESC_DRIVER_BUILD_CLI=ON \
    "

# One escd instance per physical ESC (must match esc-sysfs's num_escs=5
# modprobe.d setting: each instance N relays /dev/escN, created by that
# module for N in [0, num_escs)).
SYSTEMD_SERVICE:${PN} = "escd@0.service escd@1.service escd@2.service escd@3.service escd@4.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/escd@.service ${D}${systemd_system_unitdir}/escd@.service

    install -d ${D}${sysconfdir}/default
    for i in 0 1 2 3 4; do
        install -m 0644 ${UNPACKDIR}/escd-$i.default ${D}${sysconfdir}/default/escd-$i
    done
}

FILES:${PN} += "${systemd_system_unitdir}/escd@.service ${sysconfdir}/default/escd-0 \
                 ${sysconfdir}/default/escd-1 ${sysconfdir}/default/escd-2 \
                 ${sysconfdir}/default/escd-3 ${sysconfdir}/default/escd-4"
CONFFILES:${PN} += "${sysconfdir}/default/escd-0 ${sysconfdir}/default/escd-1 \
                     ${sysconfdir}/default/escd-2 ${sysconfdir}/default/escd-3 \
                     ${sysconfdir}/default/escd-4"

# esc-sysfs creates /dev/esc0..esc4; each escd@N waits on its own
# dev-escN.device (After=/Wants=) but doesn't hard-depend on the kernel
# module being present at install time.
RRECOMMENDS:${PN} += "esc-sysfs"
