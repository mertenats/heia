package s10;

import java.io.*;

// Context : RSA cryptography algorithm

public class Crypto {

  // ------------------------------------------------------------
  // Message file : any text file
  // Key file : line 1 : number e or d
  // line 2 : number N
  // Output file : one number per line (denotes one encrypted character)
  public static void encrypt(String msgFile, String keyFile, String outFile)
      throws IOException {
    BufferedReader fm = new BufferedReader(new FileReader(msgFile));
    BufferedReader fk = new BufferedReader(new FileReader(keyFile));
    PrintWriter fo = new PrintWriter(new FileWriter(outFile));

    String s;
    s = fk.readLine();
    long e = Long.parseLong(s);
    s = fk.readLine();
    long n = Long.parseLong(s);
    fk.close();
    int r = fm.read();   // reads one character
    while (r != -1) {
      char c = (char) r;
      fo.println(encode(c, e, n));
      r = fm.read();     // reads one character
    }
    fm.close();
    fo.close();
  }

  // ------------------------------------------------------------
  // Message file : one number per line (denotes one encrypted character)
  // Key file : line 1 : number e or d
  // line 2 : number N
  // Out file : original text
  public static void decrypt(String MsgFile, String KeyFile, String outFile)
      throws IOException {
    BufferedReader fm = new BufferedReader(new FileReader(MsgFile));
    BufferedReader fk = new BufferedReader(new FileReader(KeyFile));
    PrintWriter fo = new PrintWriter(new FileWriter(outFile));

    // reads and gets the private Key
    String s = fk.readLine();
    long d = Long.parseLong(s);
    s = fk.readLine();
    long n = Long.parseLong(s);

    // reads the crypted file
    s = fm.readLine();  // reads the first line of numbers
    while (s != null) {
      long c = Long.parseLong(s); // parses long to char
      fo.print(decode(c, d, n));  // decodes the char
      s = fm.readLine(); // reads next line
    }

    // closes the streams
    fk.close();
    fm.close();
    fo.close();
  }

  // ------------------------------------------------------------
  // public key file : line 1 : number e
  // line 2 : number N
  // private key file : line 1 : number d
  // line 2 : number N
  // code1-2 : indices of the desired prime numbers for p and q
  // (here e is chosen non-randomly)
  public static void createKeys(int code1, int code2, String publicKeyFile,
      String privateKeyFile) throws IOException {
    long p = getKthPrimeNb(code1);
    long q = getKthPrimeNb(code2);
    long n = p * q;
    long nPrime = (p - 1) * (q - 1);
    long e = getRelativePrime(nPrime);
    long d = multInverse(e, nPrime);

    PrintWriter fa = new PrintWriter(new FileWriter(publicKeyFile));
    PrintWriter fb = new PrintWriter(new FileWriter(privateKeyFile));
    System.out.println("p=" + p + ",q=" + q + ",e=" + e + ",n=" + n
        + ",nPrime=" + nPrime + ",d=" + d);
    fa.println(e);
    fa.println(n);
    fa.close();
    fb.println(d);
    fb.println(n);
    fb.close();
  }

  // ------------------------------------------------------------
  public static boolean isPrime(long n) {
    if (n < 2) {
      return false;
    }
    if (n == 2 || n == 3) {
      return true;
    }
    if (n % 2 == 0 || n % 3 == 0) {
      return false;
    }
    long sqrtN = (long) Math.sqrt(n) + 1;
    for (long i = 6L; i <= sqrtN; i += 6) {
      if (n % (i - 1) == 0 || n % (i + 1) == 0) {
        return false;
      }
    }
    return true;
  }

  // ------------------------------------------------------------
  // PRE: kth > 0
  public static long getKthPrimeNb(int kth) {
    if (kth > 0) {
      int primeNbCounter = 0;
      int i = 2; // firts prime number
      while (primeNbCounter < kth - 1) {
        i++;
        if (isPrime(i)) {
          primeNbCounter++;
        }
      }
      return i;
    }
    return -1;
  }

  // ------------------------------------------------------------
  private static long powerModulo(long base, long exp, long mod) {
    if (exp == 0)
      return 1;
    long tmp = powerModulo((base * base) % mod, exp / 2, mod);
    if (exp % 2 != 0)
      tmp = (tmp * base) % mod;
    return tmp;
  }

  // ------------------------------------------------------------
  // returns a long d such that (d * e) MODULO nPrime == 1
  // (and such that d != e)
  private static long multInverse(long e, long nPrime) {
    assert nPrime > 0 && e > 0;
    long d = 10;  // arbitrarily
    while (d * e % nPrime != 1 || d == e)
      d++;
    return d;
  }

  // ------------------------------------------------------------
  // returns a value that is relatively prime to x.
  // here we look for a prime number greater than x.
  private static long getRelativePrime(long x) {
    assert x > 0;
    long i = 1 + x / 2; // arbitrarily
    while (gcd(x, i) != 1)
      i++;
    return i;
  }

  // greatest common divisor.
  private static long gcd(long a, long b) {
    if (b == 0)
      return a;
    return gcd(b, a % b);
  }

  // ------------------------------------------------------------
  // computes (longFromChar(m) POWER exp) MODULO mod
  // RSA works only if m < mod
  public static long encode(char m, long exp, long mod) {
    long z = powerModulo((long) m, exp, mod);
    if (m >= mod)
      throw new RuntimeException("Msg code too large for this RSA key");
    return z;
  }

  // ------------------------------------------------------------
  // computes charFromLong( (m POWER exp) MODULO mod )
  public static char decode(long m, long exp, long mod) {
    long z = powerModulo(m, exp, mod);
    return (char) z;
  }

  // ------------------------------------------------------------
  public static void printUsage() {
    System.err.println("Usage: java Crypto encrypt MsgFile KeyFile OutFile");
    System.err.println(" or  : java Crypto decrypt InFile KeyFile OutFile");
    System.err.println(" or  : java Crypto create  int int "
        + " publicKeyFile privateKeyFile");
    System.exit(-1);
  }

  // ------------------------------------------------------------
  public static void main(String[] args) {
    for (int i = 1; i < 50; i++)
      System.out.print(" " + getKthPrimeNb(i));
    System.out.println();
    for (int i = -5; i < 20; i++)
      System.out.print(" " + (isPrime(i) ? ("" + i) : ""));
    if (args.length != 4 && args.length != 5)
      printUsage();
    try {
      if (args[0].equals("encrypt")) {
        if (args.length != 4)
          printUsage();
        encrypt(args[1], args[2], args[3]);
      } else if (args[0].equals("decrypt")) {
        if (args.length != 4)
          printUsage();
        decrypt(args[1], args[2], args[3]);
      } else if (args[0].equals("create")) {
        if (args.length != 5)
          printUsage();
        int code1 = Integer.parseInt(args[1]), code2 = Integer
            .parseInt(args[2]);
        createKeys(code1, code2, args[3], args[4]);
      } else
        printUsage();
    } catch (IOException e) {
      System.err.println(e);
    }
  }
}
