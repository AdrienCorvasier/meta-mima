SUMMARY = "Core AUV control pipeline: sensor read, controller and motor pthread tasks"
LICENSE = "CLOSED"

SRC_URI = "git://github.com/AdrienCorvasier/mima-core.git;protocol=https;branch=master"
# Pin to a real commit/tag before using this recipe outside of active development.
SRCREV = "${AUTOREV}"

inherit cmake
