# JDK vs. BC Benchmark

A simple Java project to benchmark the speed of JDK AES vs BouncyCastle AES (both in [CTR mode](https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation#Counter_(CTR))).

In my experience, JDK-11 performs extremely faster than BouncyCastle, since the former uses [AES-NI](https://en.wikipedia.org/wiki/AES_instruction_set) while the latter is purely Java based. (Reference: https://github.com/bcgit/bc-java/issues/221).

Snippets of JMH output on my laptop:

* JDK-11
```
Benchmark                  Mode  Cnt     Score    Error  Units
JdkVsBcBenchmark.aes_jdk  thrpt   30  3395.140 ± 42.616  ops/s
```

* BouncyCastle
```
Benchmark                 Mode  Cnt    Score   Error  Units
JdkVsBcBenchmark.aes_bc  thrpt   30  132.869 ± 0.842  ops/s
```

The results show that JDK-11 implementation of AES is over 25 times faster than BouncyCastle. 

## By the way...
JDK-11 performs ~ 3395 operations per second, where each operation corresponds to encryption 1 MB of data using AES-CTR. That is, encryption speed is about 3.3 GB/s. Comparison with OpenSSL is indeed insightful:

```
openssl speed -evp aes-128-ctr

Doing aes-128-ctr for 3s on 16 size blocks: 181395748 aes-128-ctr's in 2.98s
Doing aes-128-ctr for 3s on 64 size blocks: 100434456 aes-128-ctr's in 3.00s
Doing aes-128-ctr for 3s on 256 size blocks: 51261409 aes-128-ctr's in 3.00s
Doing aes-128-ctr for 3s on 1024 size blocks: 16451696 aes-128-ctr's in 3.00s
Doing aes-128-ctr for 3s on 8192 size blocks: 2242294 aes-128-ctr's in 3.00s
OpenSSL 1.0.2p  14 Aug 2018
built on: reproducible build, date unspecified
options:bn(64,64) rc4(16x,int) des(idx,cisc,2,long) aes(partial) idea(int) blowfish(idx)
compiler: gcc -I. -I.. -I../include -I/mingw64/include -D_WINDLL -DOPENSSL_PIC -DZLIB_SHARED -DZLIB -DOPENSSL_THREADS -D_MT -DDSO_WIN32 -DL_ENDIAN -O3 -g -Wall -DWIN32_LEAN_AND_MEAN -DUNICODE -D_UNICODE -DOPENSSL_IA32_SSE2 -DOPENSSL_BN_ASM_MONT -DOPENSSL_BN_ASM_MONT5 -DOPENSSL_BN_ASM_GF2m -DRC4_ASM -DSHA1_ASM -DSHA256_ASM -DSHA512_ASM -DMD5_ASM -DAES_ASM -DVPAES_ASM -DBSAES_ASM -DWHIRLPOOL_ASM -DGHASH_ASM -DECP_NISTZ256_ASM
The 'numbers' are in 1000s of bytes per second processed.
type             16 bytes     64 bytes    256 bytes   1024 bytes   8192 bytes
aes-128-ctr     972509.14k  2142601.73k  4374306.90k  5615512.23k  6122957.48k
```
