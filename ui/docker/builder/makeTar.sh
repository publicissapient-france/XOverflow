#!/bin/bash
set -euo pipefail

tar -cvzf "/target/xoverflow-ui.tar.gz" -C /src/build/ .
