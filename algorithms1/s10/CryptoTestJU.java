package s10;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

/**
 * Unit tests for TP: RSA cryptography algorithm
 * Tested class: Crypto
 * Tested methods: createKeys(), encrypt(), decrypt(), isPrime(), getKthPrimeNb()
 */
public class CryptoTestJU {

  static final String FILE_MSG = "tmp/file_msg";

  static final String CONTENT_RANDOM = "vz2sns9QSE5J5Fk3pJ52\noBSOiCNF6cCCX5WC4oM4\nSOWJ6cUp3Puy4yrelvzR\n8t6qEJ9bUceXgS0MD9mN\n9NbqLqFd8uR21LsP1wSS\nZw9zuDd9Smk1ThkLmBaO\nDUOEMVh3t53iRM1d6kRC\nO4ty6KQBGrIaOgC7Fal7\nDn6PHm51s4VKttkBZ1Q9\nTYKEIMOiZAfTpSlhbkSA\nYtjzAo24N1b0nBHCXhAr\nO08p6";
  static final String CONTENT_ZEROS = "000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000\n000000000000000000000000000000000000000";
  static final String CONTENT_TEXT = "The Elder Scrolls V: Skyrim Review, Say goodbye to real life.\n\nI was stacking books on a shelf in my house in Whiterun, one of Skyrim's major cities, when I noticed a weapon rack right beside it. I set a sacrificial dagger in one slot, an Orcish mace in the other. ";
  static final String CONTENT_NO_NEW_LINE = "vz2sns9QSE5J5Fk3pJ52oBSOiCNF6cCCX5WC4oM4";

  private static TestKeySet[] keySets = new TestKeySet[]{
    new TestKeySet(15, 79),
    new TestKeySet(54, 2),
    new TestKeySet(65, 3),
    new TestKeySet(87, 5)
  };

  static final Integer[] PRIME_NUMBERS = {2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,229,233,239,241,251,257,263,269,271,277,281,283,293,307,311,313,317,331,337,347,349,353,359,367};
  private static SortedSet<Integer> primeNumbersSet = new TreeSet<Integer>();
  static {
    primeNumbersSet.addAll(Arrays.asList(PRIME_NUMBERS));
  }

  @Test public void testCreateKeys() {
    //For each pair of codes...
    for(TestKeySet k:keySets) {
      try {
        createKeys(k);
        long [] u=keyFileContent(k.getFilePubKey());
        long [] r=keyFileContent(k.getFilePvtKey());
        BigInteger e=new BigInteger(Long.toString(u[0]));
        BigInteger n=new BigInteger(Long.toString(u[1]));
        BigInteger d=new BigInteger(Long.toString(r[0]));
        BigInteger n1=new BigInteger(Long.toString(r[1]));
        assertTrue(n.equals(n1));
        assertTrue(e.gcd(d).equals(BigInteger.ONE));
      } catch (IOException e) {
        fail("Internal test failure, unable to create temp files: " + e.getMessage());
      }
    }
  }

  @Test public void testEncryptDecryptWithoutNewLines() {
    for(TestKeySet k:keySets) 
      encryptThenDecrypt(k, CONTENT_NO_NEW_LINE);
  }
  
  @Test public void testEncryptDecryptRandom() {
    for(TestKeySet k:keySets) 
      encryptThenDecrypt(k, CONTENT_RANDOM);
  }
  
  @Test public void testEncryptDecryptText() {
    for(TestKeySet k:keySets) 
      encryptThenDecrypt(k, CONTENT_TEXT);
  }

  @Test public void testEncryptDecryptZeros() {
    for(TestKeySet k:keySets) 
      encryptThenDecrypt(k, CONTENT_ZEROS);
  }

  private void encryptThenDecrypt(TestKeySet k, String msg) {
    writeMsgFile(msg);
    // Encrypt test message
    try {
      createKeys(k);
      Crypto.encrypt(FILE_MSG, k.getFilePubKey(), k.getFileEncrypted());
    } catch (FileNotFoundException e){
      fail("Some required files are missing..."+ e.getMessage());
    } catch (IOException e) {
      fail("Internal test failure, unable to create temp files: " + e.getMessage());
    }
    // Decrypt test message
    try {
      Crypto.decrypt(k.getFileEncrypted(), k.getFilePvtKey(), k.getFileDecrypted());
    } catch (FileNotFoundException e){
      fail("Some required files are missing... "+ e.getMessage());
    } catch (IOException e) {
      fail("Internal test failure, unable to create temp files: " + e.getMessage());
    }
    String expectedHash=Utils.hashString(msg);
    assertEquals("Bad decrypted message.", expectedHash, Utils.hash(k.getFileDecrypted()));
  }

  private static void createKeys(TestKeySet k) throws IOException {
    File tempDir = new File("tmp");
    tempDir.mkdir();
    Crypto.createKeys(k.code1, k.code2, k.getFilePubKey(), k.getFilePvtKey());
  }

  @Test public void testGetKthPrimeNb() {
    //Test all numbers stored in PRIME_NUMBERS array
    for(int i = 1; i < PRIME_NUMBERS.length+1; i++){
      long expected = PRIME_NUMBERS[i-1];
      long given = Crypto.getKthPrimeNb(i);
      assertEquals("Wrong " + i + "th prime.", expected, given);
    }
  }

  @Test public void testIsPrime() {
    //Test all numbers until the last stored in PRIME_NUMBERS array
    for(int i = 0; i < primeNumbersSet.last(); i++){
      //If prime is listed in our set
      if(primeNumbersSet.contains(i)){
        //i is a prime number, isPrime must return TRUE
        assertTrue(i + " supposed to be a prime number.", Crypto.isPrime(i));
      }else{
        //i is not a prime number, isPrime must return FALSE
        assertFalse(i + " not supposed to be a prime number.", Crypto.isPrime(i));
      }
    }
  }
  
  @Test public void testNoNegativeNumberIsPrime() {
    for(int i = -23; i < 0; i++) {
      assertFalse(Crypto.isPrime(i));
    }
  }

  //========================================================================
  public static class TestKeySet{
    public int code1, code2;

    static final String FILE_PUBKEY = "tmp/pubkey";
    static final String FILE_PVTKEY = "tmp/pvtkey";

    public TestKeySet(int code1, int code2) {
      this.code1 = code1;
      this.code2 = code2;
    }
    public String getFilePvtKey(){ return FILE_PVTKEY + "_" + code1 + "_" + code2; }
    public String getFilePubKey(){ return FILE_PUBKEY + "_" + code1 + "_" + code2; }
    public String getFileEncrypted(){ return CryptoTestJU.FILE_MSG + "_" + code1 + "_" + code2 + "_encrypted"; }
    public String getFileDecrypted(){ return CryptoTestJU.FILE_MSG + "_" + code1 + "_" + code2 + "_decrypted"; }
  }
  
  //========================================================================
  static class Utils{
    /**
     * Read bytes of a given file.
     * @param file            Path to the file to read.
     * @return                Contained data as an array of bytes.
     * @throws IOException    If the file cannot be found or opened.
     */
    public static byte[] readBytesFromFile(String file) throws IOException{
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      InputStream is = new FileInputStream(file);
      byte[] temp = new byte[1024];
      int read;
      while((read = is.read(temp)) > 0){
        buffer.write(temp, 0, read);
      }
      is.close();
      return buffer.toByteArray();
    }

    /**
     * Computes the SHA1 hash of a file, give the hexadecimal form.
     * @param file    the path to the file
     * @return        the SHA1 hash as a string on hexadecimal form
     */
    public static String hash(String file){
      byte[] data=null;
      try {
        data = readBytesFromFile(file);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return hashBytes(data);
    }

    public static String hashString(String str){
      return hashBytes(str.getBytes());
    }
    
    public static String hashBytes(byte[] data){
      MessageDigest md;
      byte[] digest = new byte[20];
      try {
        md = MessageDigest.getInstance("SHA1");
        md.reset();
        md.update(data, 0, data.length);
        digest = md.digest();
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
      return convertToHex(digest);
    }

    /**
     * Convert an array of bytes to a human readable hexadecimal string.
     * @param data    the array of bytes to process
     * @return        the hexadecimal representation of data
     */
    public static String convertToHex(byte[] data) { 
      StringBuffer buf = new StringBuffer();
      for (int i = 0; i < data.length; i++) { 
        int halfbyte = (data[i] >>> 4) & 0x0F;
        int two_halfs = 0;
        do { 
          if ((0 <= halfbyte) && (halfbyte <= 9)) 
            buf.append((char) ('0' + halfbyte));
          else 
            buf.append((char) ('a' + (halfbyte - 10)));
          halfbyte = data[i] & 0x0F;
        } while(two_halfs++ < 1);
      } 
      return buf.toString();
    }
  }
  
  private long[] keyFileContent(String filename) throws IOException {
    long [] res=new long[2];
    BufferedReader fis=new BufferedReader(new FileReader(filename));
    String l=fis.readLine(); 
    assertTrue(l!=null);
    res[0]=Long.parseLong(l);
    l=fis.readLine(); 
    assertTrue(l!=null);
    res[1]=Long.parseLong(l);
    fis.close();
    return res;
  }
  
  private void writeMsgFile(String msg) {
    try {
      File tempDir = new File("tmp");
      tempDir.mkdir();
      BufferedWriter out = new BufferedWriter(new FileWriter(FILE_MSG));
      out.write(msg);
      out.close();
    } catch (IOException e) { 
      fail("Internal test failure, unable to create temp files: " + e.getMessage());
    }
  }

}


