package datamine.utils;

/**
 * The function below borrows the ideas at https://www.javatips.net/api/gosu-lang-master/src/main/java/gw/util/fingerprint/FP64.java.
 */
public final class DataHasher {

    // implementation constants
    // polynomials are represented with the coefficient for x^0
    // in the most significant bit
    private static final long Zero = 0L;

      /* This class provides methods that construct fingerprints of
         strings of bytes via operations in GF[2^64].  GF[2^64] is represented
         as the set polynomials of degree 64 with coefficients in Z(2),
         modulo an irreducible polynomial P of degree 64.  The internal
         representation is a 64-bit Java value of type "long".

         Let g(S) be the string obtained from S by prepending the byte 0x80
         and appending eight 0x00 bytes.  Let f(S) be the polynomial
         associated to the string g(S) viewed as a polynomial with
         coefficients in the field Z(2). The fingerprint of S is simply
         the value f(S) modulo P.

         The irreducible polynomial p used as a modulus is

                3    7    11    13    16    19    20    24    26    28
           1 + x  + x  + x   + x   + x   + x   + x   + x   + x   + x

              29    30    36    37    38    41    42    45    46    48
           + x   + x   + x   + x   + x   + x   + x   + x   + x   + x

              50    51    52    54    56    57    59    61    62    64
           + x   + x   + x   + x   + x   + x   + x   + x   + x   + x

         IrredPoly is its representation. */
    private static final long One = 0x8000000000000000L;
    private static final long IrredPoly = 0x911498AE0E66BAD6L;
    private static final long X63 = 0x1L; // coefficient of x^63
    /* This is the table used for extending fingerprints.  The
     * value ByteModTable[i] is the value to XOR into the finger-
     * print value when the byte with value "i" is shifted from
     * the top-most byte in the fingerprint. */
    private static long[] ByteModTable;

    // Initialization code
    static {
        // Maximum power needed == 64 + 8
        int plength = 72;
        long[] powerTable = new long[plength];

        long t = One;
        for (int i = 0; i < plength; i++) {
            powerTable[i] = t;

            // t = t * x
            long mask = ((t & X63) != 0) ? IrredPoly : 0;
            t = (t >>> 1) ^ mask;
        }

        // group bit-wise overflows into bytes
        ByteModTable = new long[256];
        for (int j = 0; j < ByteModTable.length; j++) {
            long v = Zero;
            for (int k = 0; k < 9; k++) {
                if ((j & (1L << k)) != 0) {
                    v ^= powerTable[(plength - 1) - k];
                }
            }
            ByteModTable[j] = v;
        }
    }

    public static long hash(String s) {
        long ret = IrredPoly;

        final int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            byte b1 = (byte) (c & 0xff);
            ret = (ret >>> 8) ^ ByteModTable[(b1 ^ (int) ret) & 0xFF];
            byte b2 = (byte) (c >>> 8);
            if (b2 != 0) {
                ret = (ret >>> 8) ^ ByteModTable[(b2 ^ (int) ret) & 0xFF];
            }
        }
        return ret;
    }
}