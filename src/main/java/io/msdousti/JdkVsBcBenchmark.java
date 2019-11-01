package io.msdousti;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openjdk.jmh.annotations.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@State(Scope.Benchmark)
public class JdkVsBcBenchmark {
    private Cipher c1, c2;
    private byte[] ptxt;

    @Setup
    public void prepare() throws Exception {
        java.security.Security.addProvider(new BouncyCastleProvider());

        // Default AES key length = 128 bit
        SecretKey sk = KeyGenerator.getInstance("AES").generateKey();
        byte[] iv = new byte[16];
        (new SecureRandom()).nextBytes(iv);
        IvParameterSpec ips = new IvParameterSpec(iv);

        String cipherName = "AES/CTR/NoPadding";

        c1 = Cipher.getInstance(cipherName);
        c1.init(Cipher.ENCRYPT_MODE, sk, ips);

        c2 = Cipher.getInstance(cipherName, "BC");
        c2.init(Cipher.ENCRYPT_MODE, sk, ips);

        ptxt = new byte[1 << 20];
    }

    @Benchmark
    @Warmup(iterations = 4, time = 10000, timeUnit = MILLISECONDS)
    @Measurement(iterations = 30, time = 200, timeUnit = MILLISECONDS)
    @Fork(value = 1)
    public void aes_jdk() throws Exception {
        c1.doFinal(ptxt);
    }

    @Benchmark
    @Warmup(iterations = 4, time = 10000, timeUnit = MILLISECONDS)
    @Measurement(iterations = 30, time = 200, timeUnit = MILLISECONDS)
    @Fork(value = 1)
    public void aes_bc() throws Exception {
        c2.doFinal(ptxt);
    }
}
