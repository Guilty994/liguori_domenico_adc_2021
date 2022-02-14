# liguori_domenico_adc_2021
Anonymous Chat

he MD5 hash for domenicoliguori-07 is : f298d34696d8dd673915a1bb95dfd097




Docker usage:
> docker build --no-cache -t [imagename] .


Master bootstrap
> docker run -e ID=0 -e MASTERIP="127.0.0.1" -it [imagename]

Peer bootstrap
> docker run -e ID=[peer id] -e MASTERIP="172.17.0.2" -it [imagename]