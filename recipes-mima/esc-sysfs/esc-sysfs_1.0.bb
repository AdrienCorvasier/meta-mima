SUMMARY = "Thin sysfs shell for the B-G431B-ESC1 ASPEP/MCP driver"
DESCRIPTION = "Out-of-tree kernel module exposing /sys/class/esc/esc0/{speed,speed_measured, \
status,fault_flags,version,start,stop,ack} and /dev/esc0. Owns no hardware and speaks no \
serial protocol itself: every access is relayed over /dev/esc0 to the escd userspace daemon \
(see the esc-driver recipe), which does the actual ASPEP/MCP work via libesc_driver."
HOMEPAGE = "https://github.com/AdrienCorvasier/mima-b-g431-esc1-driver"

LICENSE = "CLOSED"

SRC_URI = "git://github.com/AdrienCorvasier/mima-b-g431-esc1-driver.git;protocol=https;branch=master \
           file://99-esc-sysfs.rules \
           "
# Pin to a real commit/tag before using this recipe outside of active development.
SRCREV = "${AUTOREV}"

S = "${UNPACKDIR}/${BP}/kernel"

inherit module

# The upstream kernel/Makefile defaults KDIR to the host's running-kernel
# build dir (KDIR ?= /lib/modules/$(shell uname -r)/build) instead of using
# the KERNEL_SRC the module.bbclass sets up for cross builds, so it must be
# overridden here.
EXTRA_OEMAKE += "KDIR=${STAGING_KERNEL_BUILDDIR}"

# Best-effort hint understood by some kernel-module-split machinery; kept
# alongside the explicit modules-load.d file below, which is what actually
# guarantees auto-load under systemd regardless of that machinery.
KERNEL_MODULE_AUTOLOAD += "esc_sysfs"

# The upstream Makefile only has "all"/"clean" targets (no "modules_install"),
# so module.bbclass's default do_install (which runs $(MAKE) modules_install)
# doesn't apply here; install the built .ko directly instead.
do_install() {
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0644 ${B}/esc_sysfs.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/esc_sysfs.ko

    install -d ${D}${sysconfdir}/modules-load.d
    echo "esc_sysfs" > ${D}${sysconfdir}/modules-load.d/esc-sysfs.conf

    # Without this, systemd-udevd never tags /dev/esc0 for systemd, so
    # dev-esc0.device is never created and escd.service (which orders itself
    # After=/Wants= that unit) sits stuck in dependency-wait forever.
    install -d ${D}${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/99-esc-sysfs.rules ${D}${nonarch_base_libdir}/udev/rules.d/99-esc-sysfs.rules
}

FILES:${PN} += "${sysconfdir}/modules-load.d/esc-sysfs.conf ${nonarch_base_libdir}/udev/rules.d/99-esc-sysfs.rules"
CONFFILES:${PN} += "${sysconfdir}/modules-load.d/esc-sysfs.conf"
