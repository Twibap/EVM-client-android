package io.mystudy.tnn.myevmapplication.wallet;

import android.content.Context;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import io.mystudy.tnn.myevmapplication.R;

class Mnemonic {

    private static String[] WORD_LIST;
    
    Mnemonic(Context context){
        WORD_LIST = readWordList(context);
    }

    byte[] toSeed(String mnemonic, String salt){
        int iteration = 2048;

        if ( salt == null)
            salt = "mnemonic"+"";  // salt = "";    ???
        else
            salt = "mnemonic"+salt;

        return Hash.PBKDF2withHmacSHA512( mnemonic, salt, iteration);

        // API 26
//        KeySpec spec = new PBEKeySpec(mnemonic.toCharArray(), salt.getBytes(), iteration);
//        try {
//            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA512");
//            result = factory.generateSecret(spec).getEncoded();
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
//            e.printStackTrace();
//        }

    }

    String generateMnemonic(byte[] initialEntropy){

        // Seed의 Checksum 얻기
        byte[] initialEntropyChecksum;
        initialEntropyChecksum = sha256(initialEntropy);

        // Byte Seed를 Binary로 변환
        boolean[] binaryInitialEntropy = toBooleanArray(initialEntropy);

        // Byte Checksum을 Binary로 변환
        boolean[] binaryInitialEntropyChecksum = toBooleanArray(initialEntropyChecksum);

        // Seed와 Checksum 합치기
        boolean[] entropy = new boolean[ binaryInitialEntropy.length + binaryInitialEntropyChecksum.length / 32 ];
        for (int i = 0; i < entropy.length; i++){
            if (i < binaryInitialEntropy.length)
                entropy[i] = binaryInitialEntropy[i];
            else
                entropy[i] = binaryInitialEntropyChecksum[ i - binaryInitialEntropy.length ];
        }

        // Binary를 11자리 씩 10진수로 변환
        int[] index = new int[ entropy.length / 11 ];
        for (int i = 0; i < index.length; i++){
            int from = i * 11;
            boolean[] iteration = Arrays.copyOfRange(entropy, from, from+11 );

            for (int j = 0; j < iteration.length ; j++) {
                index[i] += ( iteration[j] ? ( 1 << ( iteration.length-1 - j ) ) : 0 );
            }

        }

        // 숫자를 문자로 변환
        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0 ; i < index.length ; i++ ){
            String word = WORD_LIST[ index[i] ];
            resultBuilder.append( word );

            if ( i != index.length -1 )
                resultBuilder.append(" ");
        }

        // 반환
        return resultBuilder.toString();
    }

    private static String[] readWordList(Context context) {
        return context.getResources().getStringArray(R.array.mnemonic);
    }

    private boolean[] toBooleanArray(byte[] bytes){
        boolean[] result = new boolean[ bytes.length * Byte.SIZE ];

        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < Byte.SIZE; j++) {
                int bit_index = i * Byte.SIZE + j;
                result[bit_index] = ((bytes[i] >>> (7 - j)) & 1) > 0;
            }
        }
        return result;
    }

    private static byte[] sha256(byte[] data){
        String hash_algorithm = "SHA-256";
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(hash_algorithm);
            digest.update(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        assert digest != null;
        return digest.digest();
    }
}
