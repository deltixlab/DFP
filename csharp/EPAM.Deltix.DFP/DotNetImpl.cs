using System;
using System.Diagnostics;
using System.Runtime.CompilerServices;
using System.Text;

[assembly: InternalsVisibleToAttribute("EPAM.Deltix.DFP.Test")]
namespace EPAM.Deltix.DFP
{
	internal class DotNetImpl
	{
		private const bool ToStringRemoveTrailingZeroes = true; // Controls if ToString removes trailing zeroes or not

		#region Constants
		public const UInt64 PositiveInfinity		= 0x7800000000000000UL;
		public const UInt64 NegativeInfinity		= 0xF800000000000000UL;
		public const UInt64 NaN						= 0x7C00000000000000UL;
		public const UInt64 Null					= 0xFFFFFFFFFFFFFF80UL;	// = -0x80

		public const UInt64 MinValue				= 0xF7FB86F26FC0FFFFUL;
		public const UInt64 MaxValue				= 0x77FB86F26FC0FFFFUL;

		public const UInt64 MinPositiveValue		= 0x0000000000000001UL;
		public const UInt64 MaxNegativeValue		= 0x8000000000000001UL;
		
		public const UInt64 Zero					= 0x31C0000000000000UL; // e=0, m=0, sign=0
		public const UInt64 One						= Zero + 1;		// = Zero + 1
		public const UInt64 Two						= Zero + 2;
		public const UInt64 Ten						= Zero + 10;
		public const UInt64 Hundred					= Zero + 100;
		public const UInt64 Thousand				= Zero + 1000;
		public const UInt64 Million					= Zero + 1000000;

		public const UInt64 OneTenth				= 0x31A0000000000000UL + 1;
		public const UInt64 OneHundredth			= 0x3180000000000000UL + 1;

		#endregion

		#region Classification
		public static Boolean IsNaN(UInt64 value)
		{
			return (value & NaNMask) == NaNMask;
		}

		public static Boolean IsNull(UInt64 value)
		{
			return value == Null;
		}

		public static Boolean IsInfinity(UInt64 value)
		{
			return (value & (InfinityMask | NaNMask)) == InfinityMask;
		}

		public static Boolean IsPositiveInfinity(UInt64 value)
		{
			return (value & (SignedInfinityMask | NaNMask)) == PositiveInfinity;
		}

		public static Boolean IsNegativeInfinity(UInt64 value)
		{
			return (value & (SignedInfinityMask | NaNMask)) == NegativeInfinity;
		}

		[Obsolete("IsSigned is deprecated, please use IsNegative instead for actual comparison with 0")]
		public static Boolean IsSigned(UInt64 value)
		{
			return SignBit(value);
		}

		public static Boolean IsFinite(UInt64 value)
		{
			return (value & InfinityMask) != InfinityMask;
		}

		internal static Boolean SignBit(UInt64 value)
		{
			return (Int64)value < 0;
		}

		#endregion

		#region Conversion

		public static UInt64 FromInt32(Int32 value)
		{
			Int64 longValue = value; // Fixes -Int32.MinValue
			return value >= 0 ? (0x31C00000UL << 32) | (UInt64)longValue : (0xB1C00000UL << 32) | (UInt64)(-longValue);
		}

		public static UInt64 FromUInt32(UInt32 value)
		{
			return (0x31C00000UL << 32) | value;
		}

		// Unused
		public static UInt64 FromInt16(Int16 value)
		{
			Int32 intValue = value;
			return intValue >= 0 ? (0x31C00000UL << 32) | (UInt32)value : (0xB1C00000UL << 32) | (UInt32)(-value);
		}

		public static UInt64 FromFixedPointFastUnchecked(Int64 mantissa, int numDigits)
		{
			Int64 sign = mantissa >> 63;
			return (UInt64)mantissa & SignMask | (UInt64)(mantissa + sign ^ sign) | ((UInt64)(BaseExponent - numDigits) << 53);
		}

		public static UInt64 FromFixedPointFastUnsignedUnchecked(UInt64 mantissaUlong, int numDigits)
		{
			return mantissaUlong + ((UInt64)(BaseExponent - numDigits) << 53);
		}

		public static UInt64 FromFixedPointFastUnsignedUnchecked(UInt32 mantissaUint, int numDigits)
		{
			return FromFixedPointFastUnsignedUnchecked((UInt64)mantissaUint, numDigits);
		}

		public static UInt64 FromFixedPointFast(Int32 mantissa, int numDigits)
		{
			UInt64 rv = FromFixedPointFastUnchecked(mantissa, numDigits);
			if (numDigits + (Int32.MinValue + BiasedExponentMaxValue - BaseExponent) > (Int32.MinValue + BiasedExponentMaxValue))
				throw new ArgumentException();

			return rv;
		}

		public static UInt64 FromFixedPointFastUnsigned(UInt32 mantissaUint, int numDigits)
		{
			UInt64 rv = FromFixedPointFastUnsignedUnchecked(mantissaUint, numDigits);
			if (numDigits + (Int32.MinValue + BiasedExponentMaxValue - BaseExponent) > (Int32.MinValue + BiasedExponentMaxValue))
				throw new ArgumentException();

			return rv;
		}


		public static UInt64 FromFixedPoint32(int mantissa, int numDigits)
		{
			// TODO: Unsigned comparison could be slightly faster, maybe
			return numDigits + (Int32.MinValue + BiasedExponentMaxValue - BaseExponent) > (Int32.MinValue + BiasedExponentMaxValue)
				? NativeImpl.fromFixedPoint64(mantissa, numDigits)
				: FromFixedPointFastUnchecked(mantissa, numDigits);
		}

		public static UInt64 FromFixedPointLimitedU64(UInt64 mantissa, int numDigits)
		{
			Debug.Assert(mantissa < (1UL << 53));
			return numDigits + (Int32.MinValue + BiasedExponentMaxValue - BaseExponent) > (Int32.MinValue + BiasedExponentMaxValue)
				? NativeImpl.fromFixedPoint64((Int64)mantissa, numDigits)
				: FromFixedPointFastUnsignedUnchecked(mantissa, numDigits);
		}

		public static UInt64 FromFixedPoint32(UInt32 mantissa, int numDigits)
		{
			// TODO: Unsigned comparison could be slightly faster, maybe
			return numDigits + (Int32.MinValue + BiasedExponentMaxValue - BaseExponent) > (Int32.MinValue + BiasedExponentMaxValue)
				? NativeImpl.fromFixedPoint64(mantissa, numDigits)
				: FromFixedPointFastUnsignedUnchecked(mantissa, numDigits);
		}


		public static UInt64 FromDecimalFallback(Decimal dec)
		{
			return NativeImpl.fromFloat64((double)dec);
		}

		public static UInt64 FromDecimal(Decimal dec)
		{
			unsafe
			{
				UInt64 mantissa64 = ((UInt64*)&dec)[1];
				Int32 signAndExp = ((Int16*)&dec)[1];
				if (0 == ((UInt32*)&dec)[1] && mantissa64 <= 0x20000000000000)
					return ((UInt64)signAndExp & SignMask) + mantissa64 +
					       ((UInt64)(UInt32)(BaseExponent - signAndExp) << 53);
			}

			return FromDecimalFallback(dec);
		}

		public static Decimal ToDecimalFallback(UInt64 value)
		{
			return new Decimal(NativeImpl.toFloat64(value));
		}

		public static Decimal ToDecimal(UInt64 value)
		{
			bool signBit = (Int64)value < 0;
			Int32 exponent;
			UInt64 mantissa;
			if ((~value & SpecialEncodingMask) == 0) //if ((x & SpecialEncodingMask) == SpecialEncodingMask)
			{
				Int32 exp2;
				mantissa = UnpackSpecial(value, out exp2);
				exponent = BaseExponent - exp2;
			}
			else
			{
				// Extract the exponent.
				exponent = BaseExponent - (int)(value >> ExponentShiftSmall) & (int)ShiftedExponentMask;
				// Extract the coefficient.
				mantissa = value & SmallCoefficientMask;
			}

			if ((UInt32)exponent < 29)
				return new Decimal((int)mantissa, (int)(mantissa >> 32), 0, signBit, (byte)exponent);

			return ToDecimalFallback(value);
		}

		internal static UInt64 FromDecimalFloat64(Double x)
		{
			unchecked
			{
				UInt64 y = NativeImpl.fromFloat64(x);
				// Using signed values because unsigned division optimizations are worse than signed
				// in MS .NET, esp. Core2.0 while it should be opposite
				Int64 m;
				UInt64 signAndExp;

				UInt64 notY = ~y;
				if ((SpecialEncodingMask & notY) == 0)
				{
					// Special value or large coefficient
					if ((InfinityMask & notY) == 0)
						return y; // Infinity etc.

					m = (Int64)((y & LargeCoefficientMask) + LargeCoefficientHighBits);
					//signAndExp = ((y << 2) & SmallCoefficientExponentMask) + (y & SignMask);
					signAndExp = (y & LargeCoefficientExponentMask) * 4 + (y & SignMask);
					if ((y & 1) != 0)
						goto NeedAdjustment;
				}
				else
				{
					// "Normal" value
					m = (Int64)(y & SmallCoefficientMask);
					// 16 digits + odd
					signAndExp = y & (UInt64.MaxValue << ExponentShiftSmall);
					if ((y & 1) != 0 && (UInt64)m > MaxCoefficient / 10 + 1)
						goto NeedAdjustment;

					if (0 == m)
						return Zero;
				}

				NeedCanonize:
				for (Int64 n = m; ;)
				{
					Int64 mNext = n / 10;
					if (mNext * 10 != n)
						return signAndExp + (UInt64)n;

					n = mNext;
					signAndExp += 1L << ExponentShiftSmall;
				}

				NeedAdjustment:
				// Check the last digit

				Int64 m1 = m + 1;
				m = m1 / 10;
				if (m1 - m * 10 > 2)
					return y;

				signAndExp += 1L << ExponentShiftSmall;
				if (NativeImpl.toFloat64(signAndExp + (UInt64)m) != x)
					return y;

				goto NeedCanonize;
			}
		}

		#endregion

		#region Comparison

		public static Boolean IsZero(UInt64 value)
		{
			if (!IsFinite(value))
				return false;
			if ((value & SteeringBitsMask) == SteeringBitsMask)
				return ((value & MaskBinarySig2) | MaskBinaryOr2) > 9999999999999999L;
			return (value & MaskBinarySig1) == 0;
		}

		public static Boolean IsPositive(UInt64 value)
		{
			if ((value & NaNMask) == NaNMask)
				return false;
			if (IsZero(value))
				return false;
			return (value & SignMask) == 0;
		}

		public static Boolean IsNegative(UInt64 value)
		{
			if ((value & NaNMask) == NaNMask)
				return false;
			if (IsZero(value))
				return false;
			return (value & SignMask) != 0;
		}

		public static Boolean IsNonPositive(UInt64 value)
		{
			if ((value & NaNMask) == NaNMask)
				return false;
			if (IsZero(value))
				return true;
			return (value & SignMask) != 0;
		}

		public static Boolean IsNonNegative(UInt64 value)
		{
			if ((value & NaNMask) == NaNMask)
				return false;
			if (IsZero(value))
				return true;
			return (value & SignMask) == 0;
		}

		#endregion

		#region Arithmetic

		public static UInt64 Negate(UInt64 value)
		{
			return value ^ SignMask;
		}

		public static UInt64 Abs(UInt64 value)
		{
			return value & ~SignMask;
		}

		#endregion

		#region Rounding
		#endregion
		#region Special
		#endregion
		#region Formatting & Parsing

		public static String ToString(UInt64 value)
		{ 
			if (!IsFinite(value))
			{
				return
					IsInfinity(value) ?
						(SignBit(value) ? "-Infinity" : "Infinity")
					: IsNaN(value) ?
						(SignBit(value) ? "SNaN" : "NaN")
					: "?";
			}

			Int32 exponent;
			Boolean isNegative = (Int64)value < 0;
			Int64 coefficient; // Unsigned div by constant in not optimized by .NET
			if ((~value & SpecialEncodingMask) == 0) //if ((x & SpecialEncodingMask) == SpecialEncodingMask)
			{
				Int32 exp2;
				coefficient = (Int64)UnpackSpecial(value, out exp2);
				exponent = exp2;

			}
			else
			{
				// Extract the exponent.
				exponent = (int)(value >> ExponentShiftSmall) & (int)ShiftedExponentMask;
				// Extract the coefficient.
				coefficient = (Int64)(value & SmallCoefficientMask);
			}

			unsafe
			{
				// TODO: Special case possible for mantissa = 0, otherwise will be printed according to common rules, w/o normalization
#pragma warning disable CS0162 // Unreachable code detected
				if (ToStringRemoveTrailingZeroes)
					if (0 == coefficient)
						return "0";

				if (exponent >= BaseExponent)
				{
					if (!ToStringRemoveTrailingZeroes)
						if (0 == coefficient)
							return "0";

					int nZeros = exponent - BaseExponent;
					int nAlloc = exponent + (20 - BaseExponent);
					char* s = stackalloc char[nAlloc];
					char* e = s + nAlloc - 2, p = e - nZeros;

					for (int i = nZeros; i != 0; --i)
						p[i] = '0';

					do {
						// This is to make the code generator generate 1 DIV instead of 2
						Int64 old = coefficient + '0';
						coefficient /= 10;
						*p-- = (char)(old - coefficient * 10); // = [old - new * 10]
					} while (coefficient != 0);

					if ((Int64)value < 0)
						*p-- = '-';

					return new string(p + 1, 0, (int)(e - p));
				} else {
					int dotPos = BaseExponent - exponent;
					int nAlloc = (20 + BaseExponent) - exponent;
					char* s = stackalloc char[nAlloc];
					char* p = s + nAlloc - 2, e = p;

					do
					{
						Int64 old = coefficient + '0';
						coefficient /= 10;
						*p = '.';
						p += 0 == dotPos-- ? -1: 0; // Hopefully branch-free method to insert decimal dot
						*p-- = (char)(old - coefficient * 10); // = [old - new * 10]
					} while (coefficient != 0);
					// Haven't placed the dot yet?
					if (dotPos >= 0)
					{
						for (; dotPos > 0; --dotPos)
							*p-- = '0';
						p[0] = '.';
						p[-1] = '0';
						p -= 2;
					}

					if ((Int64)value < 0)
						*p-- = '-';

					if (ToStringRemoveTrailingZeroes)
					{
						if ('0' == *e)
						{
							while ('0' == *--e) { }
							if ('.' == *e)
								--e;
						}
					}

					return new string(p + 1, 0, (int)(e - p));
				}
#pragma warning restore CS0162
			}
		}

		internal static String ToDebugString(UInt64 value)
		{
			bool signBit = (Int64)value < 0;
			Int32 exponent;
			UInt64 coefficient = Unpack(value, out exponent);
			StringBuilder sb = new StringBuilder(64)
				.Append("0x").Append(BitConverter.ToString(BitConverter.GetBytes(value)))
				.Append("[").Append(signBit ? '-' : '+').Append(',')
				.Append(exponent).Append(',').Append(coefficient).Append(']');

			return sb.ToString();
		}

	#endregion
	#region Conversion

	#endregion

	#region Private constants

		private const UInt64 SignMask				= 0x8000000000000000UL;

		private const UInt64 InfinityMask			= 0x7800000000000000UL;
		private const UInt64 SignedInfinityMask		= 0xF800000000000000UL;

		private const UInt64 NaNMask				= 0x7C00000000000000UL;
		private const UInt64 SignalingNaNMask		= 0xFC00000000000000UL;

		private const UInt64 SteeringBitsMask		= 0x6000000000000000UL;
		private const UInt64 MaskBinarySig1			= 0x001FFFFFFFFFFFFFUL;
		private const UInt64 MaskBinarySig2			= 0x0007FFFFFFFFFFFFUL;
		private const UInt64 MaskBinaryOr2			= 0x0020000000000000UL;

		private const UInt64 SpecialEncodingMask	= 0x6000000000000000UL;

		private const UInt64 LargeCoefficientMask	= 0x0007FFFFFFFFFFFFUL;
		private const UInt64 LargeCoefficientHighBits = 0x0020000000000000UL;
		private const UInt64 SmallCoefficientMask	= 0x001FFFFFFFFFFFFFUL;

		private const UInt64 MinCoefficient			= 0UL;
		private const UInt64 MaxCoefficient			= 9999999999999999UL;

		private const UInt64 ShiftedExponentMask	= 0x3FF;
		private const Int32 ExponentShiftLarge		= 51;
		private const Int32 ExponentShiftSmall		= 53;
		private const UInt64 LargeCoefficientExponentMask = ShiftedExponentMask << ExponentShiftLarge;
		private const UInt64 SmallCoefficientExponentMask = ShiftedExponentMask << ExponentShiftSmall;

		private const Int32 MinExponent				= -383;
		private const Int32 MaxExponent				= 384;
		private const Int32 BiasedExponentMaxValue	= 767;
		private const Int32 BaseExponent			= 0x18E;

		private const Int32 MaxFormatDigits			= 16;
		private const Int32 BidRoundingToNearest	= 0x00000;
		private const Int32 BidRoundingDown			= 0x00001;
		private const Int32 BidRoundingUp			= 0x00002;
		private const Int32 BidRoundingToZero		= 0x00003;
		private const Int32 BidRoundingTiesAway		= 0x00004;

		private static readonly UInt64[,] BidRoundConstTable = new UInt64[,]
		{
			{    // RN
				0,    // 0 extra digits
				5,    // 1 extra digits
				50,    // 2 extra digits
				500,    // 3 extra digits
				5000,    // 4 extra digits
				50000,    // 5 extra digits
				500000,    // 6 extra digits
				5000000,    // 7 extra digits
				50000000,    // 8 extra digits
				500000000,    // 9 extra digits
				5000000000,    // 10 extra digits
				50000000000,    // 11 extra digits
				500000000000,    // 12 extra digits
				5000000000000,    // 13 extra digits
				50000000000000,    // 14 extra digits
				500000000000000,    // 15 extra digits
				5000000000000000,    // 16 extra digits
				50000000000000000,    // 17 extra digits
				500000000000000000    // 18 extra digits
			},
			{    // RD
				0,    // 0 extra digits
				0,    // 1 extra digits
				0,    // 2 extra digits
				0,    // 3 extra digits
				0,    // 4 extra digits
				0,    // 5 extra digits
				0,    // 6 extra digits
				0,    // 7 extra digits
				0,    // 8 extra digits
				0,    // 9 extra digits
				0,    // 10 extra digits
				0,    // 11 extra digits
				0,    // 12 extra digits
				0,    // 13 extra digits
				0,    // 14 extra digits
				0,    // 15 extra digits
				0,    // 16 extra digits
				0,    // 17 extra digits
				0    // 18 extra digits
			},
			{    // round to Inf
				0,    // 0 extra digits
				9,    // 1 extra digits
				99,    // 2 extra digits
				999,    // 3 extra digits
				9999,    // 4 extra digits
				99999,    // 5 extra digits
				999999,    // 6 extra digits
				9999999,    // 7 extra digits
				99999999,    // 8 extra digits
				999999999,    // 9 extra digits
				9999999999,    // 10 extra digits
				99999999999,    // 11 extra digits
				999999999999,    // 12 extra digits
				9999999999999,    // 13 extra digits
				99999999999999,    // 14 extra digits
				999999999999999,    // 15 extra digits
				9999999999999999,    // 16 extra digits
				99999999999999999,    // 17 extra digits
				999999999999999999    // 18 extra digits
			},
			{    // RZ
				0,    // 0 extra digits
				0,    // 1 extra digits
				0,    // 2 extra digits
				0,    // 3 extra digits
				0,    // 4 extra digits
				0,    // 5 extra digits
				0,    // 6 extra digits
				0,    // 7 extra digits
				0,    // 8 extra digits
				0,    // 9 extra digits
				0,    // 10 extra digits
				0,    // 11 extra digits
				0,    // 12 extra digits
				0,    // 13 extra digits
				0,    // 14 extra digits
				0,    // 15 extra digits
				0,    // 16 extra digits
				0,    // 17 extra digits
				0    // 18 extra digits
			},
			{    // round ties away from 0
				0,    // 0 extra digits
				5,    // 1 extra digits
				50,    // 2 extra digits
				500,    // 3 extra digits
				5000,    // 4 extra digits
				50000,    // 5 extra digits
				500000,    // 6 extra digits
				5000000,    // 7 extra digits
				50000000,    // 8 extra digits
				500000000,    // 9 extra digits
				5000000000,    // 10 extra digits
				50000000000,    // 11 extra digits
				500000000000,    // 12 extra digits
				5000000000000,    // 13 extra digits
				50000000000000,    // 14 extra digits
				500000000000000,    // 15 extra digits
				5000000000000000,    // 16 extra digits
				50000000000000000,    // 17 extra digits
				500000000000000000    // 18 extra digits
			}
		};

		private static readonly UInt64[,] BidReciprocals10_128 = {
			{0, 0},                                      // 0 extra digits
			{0x3333333333333334, 0x3333333333333333},    // 1 extra digit
			{0x51eb851eb851eb86, 0x051eb851eb851eb8},    // 2 extra digits
			{0x3b645a1cac083127, 0x0083126e978d4fdf},    // 3 extra digits
			{0x4af4f0d844d013aa, 0x00346dc5d6388659},    //  10^(-4) * 2^131
			{0x08c3f3e0370cdc88, 0x0029f16b11c6d1e1},    //  10^(-5) * 2^134
			{0x6d698fe69270b06d, 0x00218def416bdb1a},    //  10^(-6) * 2^137
			{0xaf0f4ca41d811a47, 0x0035afe535795e90},    //  10^(-7) * 2^141
			{0xbf3f70834acdaea0, 0x002af31dc4611873},    //  10^(-8) * 2^144
			{0x65cc5a02a23e254d, 0x00225c17d04dad29},    //  10^(-9) * 2^147
			{0x6fad5cd10396a214, 0x0036f9bfb3af7b75},    // 10^(-10) * 2^151
			{0xbfbde3da69454e76, 0x002bfaffc2f2c92a},    // 10^(-11) * 2^154
			{0x32fe4fe1edd10b92, 0x00232f33025bd422},    // 10^(-12) * 2^157
			{0x84ca19697c81ac1c, 0x00384b84d092ed03},    // 10^(-13) * 2^161
			{0x03d4e1213067bce4, 0x002d09370d425736},    // 10^(-14) * 2^164
			{0x3643e74dc052fd83, 0x0024075f3dceac2b},    // 10^(-15) * 2^167
			{0x56d30baf9a1e626b, 0x0039a5652fb11378},    // 10^(-16) * 2^171
			{0x12426fbfae7eb522, 0x002e1dea8c8da92d},    // 10^(-17) * 2^174
			{0x41cebfcc8b9890e8, 0x0024e4bba3a48757},    // 10^(-18) * 2^177
			{0x694acc7a78f41b0d, 0x003b07929f6da558},    // 10^(-19) * 2^181
			{0xbaa23d2ec729af3e, 0x002f394219248446},    // 10^(-20) * 2^184
			{0xfbb4fdbf05baf298, 0x0025c768141d369e},    // 10^(-21) * 2^187
			{0x2c54c931a2c4b759, 0x003c7240202ebdcb},    // 10^(-22) * 2^191
			{0x89dd6dc14f03c5e1, 0x00305b66802564a2},    // 10^(-23) * 2^194
			{0xd4b1249aa59c9e4e, 0x0026af8533511d4e},    // 10^(-24) * 2^197
			{0x544ea0f76f60fd49, 0x003de5a1ebb4fbb1},    // 10^(-25) * 2^201
			{0x76a54d92bf80caa1, 0x00318481895d9627},    // 10^(-26) * 2^204
			{0x921dd7a89933d54e, 0x00279d346de4781f},    // 10^(-27) * 2^207
			{0x8362f2a75b862215, 0x003f61ed7ca0c032},    // 10^(-28) * 2^211
			{0xcf825bb91604e811, 0x0032b4bdfd4d668e},    // 10^(-29) * 2^214
			{0x0c684960de6a5341, 0x00289097fdd7853f},    // 10^(-30) * 2^217
			{0x3d203ab3e521dc34, 0x002073accb12d0ff},    // 10^(-31) * 2^220
			{0x2e99f7863b696053, 0x0033ec47ab514e65},    // 10^(-32) * 2^224
			{0x587b2c6b62bab376, 0x002989d2ef743eb7},    // 10^(-33) * 2^227
			{0xad2f56bc4efbc2c5, 0x00213b0f25f69892},    // 10^(-34) * 2^230
			{0x0f2abc9d8c9689d1, 0x01a95a5b7f87a0ef},    // 35 extra digits
		};

		private static Int32[] BidRecipeScale = {
			129 - 128,    // 1
			129 - 128,    // 1/10
			129 - 128,    // 1/10^2
			129 - 128,    // 1/10^3
			3,    // 131 - 128
			6,    // 134 - 128
			9,    // 137 - 128
			13,    // 141 - 128
			16,    // 144 - 128
			19,    // 147 - 128
			23,    // 151 - 128
			26,    // 154 - 128
			29,    // 157 - 128
			33,    // 161 - 128
			36,    // 164 - 128
			39,    // 167 - 128
			43,    // 171 - 128
			46,    // 174 - 128
			49,    // 177 - 128
			53,    // 181 - 128
			56,    // 184 - 128
			59,    // 187 - 128
			63,    // 191 - 128

			66,    // 194 - 128
			69,    // 197 - 128
			73,    // 201 - 128
			76,    // 204 - 128
			79,    // 207 - 128
			83,    // 211 - 128
			86,    // 214 - 128
			89,    // 217 - 128
			92,    // 220 - 128
			96,    // 224 - 128
			99,    // 227 - 128
			102,    // 230 - 128
			109,    // 237 - 128, 1/10^35
		};

		#endregion

		public static UInt64 Canonize(UInt64 value)
		{
			Int32 exponent;
			Boolean isNegative = (Int64)value < 0;
			// Unsigned division by constant is not optimized properly on the current version of MS .NET
			// The number will not become negative in any case
			Int64 mantissa;
			if ((~value & SpecialEncodingMask) == 0) //if ((x & SpecialEncodingMask) == SpecialEncodingMask)
			{
				Int32 exp2;
				mantissa = (Int64)UnpackSpecial(value, out exp2);
				exponent = exp2;

			}
			else
			{
				// Extract the exponent.
				exponent = (int)(value >> ExponentShiftSmall) & (int)ShiftedExponentMask;
				// Extract the coefficient.
				mantissa = (Int64)(value & SmallCoefficientMask);
			}

			if (mantissa == 0)
				return Zero;

			while (true)
			{
				Int64 c2 = mantissa / 10;
				if (mantissa != c2 * 10)
					break;

				mantissa = c2;
				++exponent;
			}

			return Pack(isNegative, exponent, (UInt64)mantissa, BidRoundingToNearest);
		}


		public static UInt64 Pack(bool isNegative, Int32 exponent, UInt64 coefficient, Int32 roundingMode)
		{
			UInt64 sgn = isNegative ? SignMask : 0;

			UInt64 Q_low_0, Q_low_1;
			UInt64 QH, r, mask, _C64, remainder_h;

			int extra_digits, amount, amount2;

			if (coefficient > 9999999999999999UL)
			{
				exponent++;
				coefficient = 1000000000000000UL;
			}

			// Check for possible underflow/overflow.
			if (exponent > BiasedExponentMaxValue || exponent < 0)
			{
				if (exponent < 0)
				{
					// Underflow.
					if (exponent + MaxFormatDigits < 0)
					{
						if (roundingMode == BidRoundingDown && isNegative)
							return 0x8000000000000001UL;
						if (roundingMode == BidRoundingUp && !isNegative)
							return 1L;
						return sgn;
					}

					if (isNegative && (roundingMode == BidRoundingDown || roundingMode == BidRoundingUp))
						roundingMode = 3 - roundingMode;

					// Get digits to be shifted out
					extra_digits = -exponent;
					coefficient += BidRoundConstTable[roundingMode, extra_digits];

					// Get coefficient * (2^M[extra_digits])/10^extra_digits
					{
						ulong ALBL_0, ALBL_1, ALBH_0, ALBH_1, QM2_0, QM2_1;
						{
							ulong CXH, CXL, CYH, CYL, PL, PH, PM, PM2;
							CXH = coefficient >> 32;
							CXL = (uint)((coefficient));
							CYH = BidReciprocals10_128[extra_digits, 1] >> 32;
							CYL = (uint)BidReciprocals10_128[extra_digits, 1];
							PM = CXH * CYL;
							PH = CXH * CYH;
							PL = CXL * CYL;
							PM2 = CXL * CYH;
							PH += (PM >> 32);
							PM = (PM & 0xFFFFFFFFL) + PM2 + (PL >> 32);
							ALBH_1 = PH + (PM >> 32);
							ALBH_0 = (PM << 32) + (uint)PL;
						}
						{
							ulong CXH, CXL, CYH, CYL, PL, PH, PM, PM2;
							CXH = ((coefficient)) >> 32;
							CXL = (uint)((coefficient));
							CYH = BidReciprocals10_128[extra_digits, 0] >> 32;
							CYL = (uint)BidReciprocals10_128[extra_digits, 0];
							PM = CXH * CYL;
							PH = CXH * CYH;
							PL = CXL * CYL;
							PM2 = CXL * CYH;
							PH += (PM >> 32);
							PM = (PM & 0xFFFFFFFFL) + PM2 + (PL >> 32);
							ALBL_1 = PH + (PM >> 32);
							ALBL_0 = (PM << 32) + (PL & 0xFFFFFFFFL);
						}
						Q_low_0 = ALBL_0;
						{
							ulong R64H;
							R64H = ALBH_1;
							QM2_0 = ALBL_1 + ALBH_0;
							if (QM2_0 < ALBL_1)
								R64H++;
							QM2_1 = R64H;
						}
						Q_low_1 = QM2_0;
						QH = QM2_1;
					}

					// Now get P/10^extra_digits: shift Q_high right by M[extra_digits]-128
					amount = BidRecipeScale[extra_digits];

					_C64 = QH >> amount;

					if (roundingMode == BidRoundingToNearest)
						if ((_C64 & 1) != 0)
						{
							// Check whether fractional part of initial_P/10^extra_digits is exactly .5
							// Get remainder
							amount2 = 64 - amount;
							remainder_h = 0;
							remainder_h--;
							remainder_h >>= amount2;
							remainder_h = remainder_h & QH;

							if (remainder_h == 0L
								&& (Q_low_1 < BidReciprocals10_128[extra_digits, 1]
								|| (Q_low_1 == BidReciprocals10_128[extra_digits, 1]
								&& Q_low_0 < BidReciprocals10_128[extra_digits, 0])))
							{
								_C64--;
							}
						}
					return sgn | _C64;
				}
				if (coefficient == 0L)
				{
					if (exponent > BiasedExponentMaxValue)
						exponent = BiasedExponentMaxValue;
				}
				while (coefficient < 1000000000000000L && exponent >= 3 * 256)
				{
					exponent--;
					coefficient = (coefficient << 3) + (coefficient << 1); // *= 10
				}
				if (exponent > BiasedExponentMaxValue)
				{
					// Overflow
					r = sgn | InfinityMask;
					if (roundingMode == BidRoundingDown)
					{
						if (!isNegative)
							r = MaxValue;
					} else
					if (roundingMode == BidRoundingToZero) { 
						r = sgn | MaxValue;
					} else if (roundingMode == BidRoundingUp) { 	
						if (isNegative)
							r = MinValue;
					}
					return r;
				}
			}

			mask = 1;
			mask <<= ExponentShiftSmall;

			// Check whether coefficient fits in 10 * 5 + 3 bits.
			if (coefficient < mask)
			{
				r = (ulong)exponent;
				r <<= ExponentShiftSmall;
				r |= (coefficient | sgn);
				return r;
			}
			// Special format.

			// Eliminate the case coefficient == 10^16 after rounding.
			if (coefficient == 10000000000000000)
			{
				r = (ulong)(exponent + 1);
				r <<= ExponentShiftSmall;
				r |= (1000000000000000 | sgn);
				return r;
			}

			r = (ulong)exponent;
			r <<= ExponentShiftLarge;
			r |= (sgn | SpecialEncodingMask);

			// Add coefficient, without leading bits.
			mask = (mask >> 2) - 1;
			coefficient &= mask;
			r |= coefficient;
			return r;
		}


		public static UInt64 UnpackSpecial(UInt64 x, out Int32 exponent)
		{
			UInt64 coefficient;
			if ((x & InfinityMask) == InfinityMask)
			{
				exponent = 0;
				coefficient = x & 0xFE03FFFFFFFFFFFFUL;
				if ((x & 0x0003FFFFFFFFFFFFUL) >= 1000000000000000UL)
					coefficient = x & 0xFE00000000000000UL;
				if ((x & NaNMask) == InfinityMask)
					coefficient = x & SignedInfinityMask;
				return 0;
			}

			// Extract the exponent.
			exponent = (int)(x >> ExponentShiftLarge) & (int)ShiftedExponentMask;

			// Check for non-canonical values.
			coefficient = (x & LargeCoefficientMask) | LargeCoefficientHighBits;
			if (coefficient >= 10000000000000000UL)
				coefficient = 0;
			
			return coefficient;
		}

		// Older version, currently unused
		public static UInt64 Unpack(UInt64 x, out Boolean signBit, out Int32 exponent)
		{
			signBit = (Int64)x < 0;
			if ((x & SpecialEncodingMask) != SpecialEncodingMask)
			{
				// Extract the exponent.
				exponent = (int)(x >> ExponentShiftSmall) & (int)ShiftedExponentMask;
				// Extract the coefficient.
				return x & SmallCoefficientMask;
			}

			return UnpackSpecial(x, out exponent);
		}

		// Without sign
		public static UInt64 Unpack(UInt64 x, out Int32 exponent)
		{
			if ((~x & SpecialEncodingMask) != 0) //if ((x & SpecialEncodingMask) != SpecialEncodingMask)
			{
				// Extract the exponent.
				exponent = (int)(x >> ExponentShiftSmall) & (int)ShiftedExponentMask;
				// Extract the coefficient.
				return x & SmallCoefficientMask;
			}

			return UnpackSpecial(x, out exponent);
			//Int32 exp2;
			//UInt64 coefficient = UnpackSpecial(x, out exp2);
			//exponent = exp2;
			//return coefficient;
		}
	}
}
