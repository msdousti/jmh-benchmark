JDK vs. BC Benchmark
--

A simple Java project to benchmark the speed of JDK AES vs BouncyCastle AES (both in [CTR mode](https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation#Counter_(CTR))).

In my experience, JDK-11 performs extremely faster than BouncyCastle, since the former uses [AES-NI](https://en.wikipedia.org/wiki/AES_instruction_set) while the latter is purely Java based. (Reference: https://github.com/bcgit/bc-java/issues/221).

Snippets of JMH output on my laptop:

## JDK-11
```
Benchmark                  Mode  Cnt     Score    Error  Units
JdkVsBcBenchmark.aes_jdk  thrpt   30  3395.140 ± 42.616  ops/s
```

## BouncyCastle
```
Benchmark                 Mode  Cnt    Score   Error  Units
JdkVsBcBenchmark.aes_bc  thrpt   30  132.869 ± 0.842  ops/s
```

The results show that JDK-11 implementation of AES is over 25 times faster than BouncyCastle. 
