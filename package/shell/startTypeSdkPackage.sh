#!/bin/bash
cd /data/typesdk/package/server
pm2 start package_task.js -i max
