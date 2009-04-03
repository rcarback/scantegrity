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
     ../scanimage --batch="scan-$NUM-%d.tiff" --mode Lineart --format=tiff --resolution 150
     #$./scanimage --mode Lineart --format=tiff --source "ADF Duplex" --resolution 150 > out.tiff

    NUM=$((NUM+1))
done