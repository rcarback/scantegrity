#!/bin/bash

mkdir /mnt/scantegritytmpfs
mount -t tmpfs tmpfs /mnt/scantegritytmpfs
mkdir /mnt/scantegritytmpfs/images
mkdir /mnt/scantegritytmpfs/backup
cp /usr/bin/scanimage /mnt/scantegritytmpfs/

chmod -R 777 /mnt/scantegritytmpfs

cd /mnt/scantegritytmpfs/images

NUM=0
while true; do
	# brightness must be around 55 for scan to work properly
     ../scanimage --batch="scan-$NUM-%d.tiff" --mode Lineart --format=tiff  --brightness 55 --resolution 150
     #$./scanimage --mode Lineart --format=tiff --source "ADF Duplex" --resolution 150 > out.tiff

    NUM=$((NUM+1))
done