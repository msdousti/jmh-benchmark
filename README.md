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

## Comparison with OpenSSL
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

As the output indicates, OpenSSL runs a series of benchmarks on various input sizes, from 16 bytes to 8192 bytes. In order to compare this with our Java code, we need to adapt the input size (1 MB) in the code. Better yet, we can use [OpenSSL 1.1.1, which supports `-bytes` argument](https://www.openssl.org/docs/man1.1.1/man1/openssl-speed.html):

```
openssl.exe speed -evp aes-128-ctr -bytes 1073741824

Doing aes-128-ctr for 3s on 1073741824 size blocks: 17 aes-128-ctr's in 3.08s
OpenSSL 1.1.1c  28 May 2019
built on: Wed May 29 04:30:04 2019 UTC
options:bn(64,64) rc4(16x,int) des(long) aes(partial) idea(int) blowfish(ptr)
compiler: cl /Z7 /Fdossl_static.pdb /Gs0 /GF /Gy /MD /W3 /wd4090 /nologo /O2 -DL_ENDIAN -DOPENSSL_PIC -DOPENSSL_CPUID_OBJ -DOPENSSL_IA32_SSE2 -DOPENSSL_BN_ASM_MONT -DOPENSSL_BN_ASM_MONT5 -DOPENSSL_BN_ASM_GF2m -DSHA1_ASM -DSHA256_ASM -DSHA512_ASM -DKECCAK1600_ASM -DRC4_ASM -DMD5_ASM -DAES_ASM -DVPAES_ASM -DBSAES_ASM -DGHASH_ASM -DECP_NISTZ256_ASM -DX25519_ASM -DPOLY1305_ASM -D_USING_V110_SDK71_ -D_WINSOCK_DEPRECATED_NO_WARNINGS
The 'numbers' are in 1000s of bytes per second processed.
type        1073741824 bytes
aes-128-ctr    5930107.13k
```

Evidently, OpenSSL is almost twice as fast as JDK-11.
