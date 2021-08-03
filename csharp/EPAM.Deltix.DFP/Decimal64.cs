using System;
using System.Globalization;
using System.Runtime.Serialization;
using System.Text;

namespace EPAM.Deltix.DFP
{
	[Serializable()]
	public struct Decimal64 : IComparable<Decimal64>, IEquatable<Decimal64>, ISerializable
	{
		#region Constants
		public static readonly Decimal64 Null		= new Decimal64(DotNetImpl.Null);

		public static readonly Decimal64 NaN		= new Decimal64(DotNetImpl.NaN);

		public static readonly Decimal64 PositiveInfinity = new Decimal64(DotNetImpl.PositiveInfinity);
		public static readonly Decimal64 NegativeInfinity = new Decimal64(DotNetImpl.NegativeInfinity);

		public static readonly Decimal64 MinValue	= new Decimal64(DotNetImpl.MinValue);
		public static readonly Decimal64 MaxValue	= new Decimal64(DotNetImpl.MaxValue);

		public static readonly Decimal64 MinPositiveValue = new Decimal64(DotNetImpl.MinPositiveValue);
		public static readonly Decimal64 MaxNegativeValue = new Decimal64(DotNetImpl.MaxNegativeValue);

		public static readonly Decimal64 Zero		= new Decimal64(DotNetImpl.Zero);
		public static readonly Decimal64 One		= new Decimal64(DotNetImpl.One);
		public static readonly Decimal64 Two		= new Decimal64(DotNetImpl.Two);
		public static readonly Decimal64 Ten		= new Decimal64(DotNetImpl.Ten);
		public static readonly Decimal64 Hundred	= new Decimal64(DotNetImpl.Hundred);
		public static readonly Decimal64 Thousand	= new Decimal64(DotNetImpl.Thousand);
		public static readonly Decimal64 Million	= new Decimal64(DotNetImpl.Million);

		public static readonly Decimal64 OneTenth	= new Decimal64(DotNetImpl.OneTenth);
		public static readonly Decimal64 OneHundredth = new Decimal64(DotNetImpl.OneHundredth);

		#endregion

		public UInt64 Bits { get; }

		private Decimal64(UInt64 value)
		{
			Bits = value;
		}

		#region Standard overloads

		public override String ToString()
		{
			return DotNetImpl.ToString(Bits);
			//return ((Double)this).ToString(CultureInfo.InvariantCulture);
		}

		public override Boolean Equals(Object obj)
		{
			return obj is Decimal64 && Equals((Decimal64)obj);
		}

		public override Int32 GetHashCode()
		{
			UInt64 bits = Canonize().Bits;
			return bits.GetHashCode();
		}

		#endregion

		#region Conversion

		public static Decimal64 FromFixedPoint(int mantissa, int numberOfDigits)
		{
			return new Decimal64(DotNetImpl.FromFixedPoint32(mantissa, numberOfDigits));
		}

		public static Decimal64 FromFixedPoint(uint mantissa, int numberOfDigits)
		{
			return new Decimal64(DotNetImpl.FromFixedPoint32(mantissa, numberOfDigits));
		}

		public static Decimal64 FromFixedPoint(long mantissa, int numberOfDigits)
		{
			// TODO: More optimizations
			return new Decimal64(
				0 == (mantissa & (-1L << 53))
				? DotNetImpl.FromFixedPointLimitedU64((UInt64)mantissa, numberOfDigits)
				: NativeImpl.fromFixedPoint64(mantissa, numberOfDigits));
		}

		public Int64 ToFixedPoint(int numberOfDigits)
		{
			return NativeImpl.toFixedPoint(Bits, numberOfDigits);
		}

		public static Decimal64 FromLong(long value)
		{
			return new Decimal64(NativeImpl.fromInt64(value));
		}

		public long ToLong()
		{
			return NativeImpl.toInt64(Bits);
		}

		public static Decimal64 FromULong(ulong value)
		{
			return new Decimal64(NativeImpl.fromUInt64(value));
		}

		public ulong ToULong()
		{
			return NativeImpl.toUInt64(Bits);
		}

		public static Decimal64 FromInt(int value)
		{
			return new Decimal64(DotNetImpl.FromInt32(value));
		}

		public int ToInt()
		{
			return (int)NativeImpl.toInt64(Bits);
		}

		public static Decimal64 FromUInt(uint value)
		{
			return new Decimal64(DotNetImpl.FromUInt32(value));
		}

		public uint ToUInt()
		{
			return (uint)NativeImpl.toUInt64(Bits);
		}

		public short ToShort()
		{
			return (short)NativeImpl.toInt64(Bits);
		}

		public ushort ToUShort()
		{
			return (ushort)NativeImpl.toUInt64(Bits);
		}

		public sbyte ToSByte()
		{
			return (sbyte)NativeImpl.toInt64(Bits);
		}

		public byte ToByte()
		{
			return (byte)NativeImpl.toUInt64(Bits);
		}

		public static Decimal64 FromUnderlying(UInt64 bits)
		{
			return new Decimal64(bits);
		}

		public UInt64 ToUnderlying()
		{
			return Bits;
		}

		public static Decimal64 FromDouble(Double value)
		{
			return new Decimal64(NativeImpl.fromFloat64(value));
		}

		public static Decimal64 FromDecimalDouble(Double value)
		{
			return new Decimal64(DotNetImpl.FromDecimalFloat64(value));
		}

		public Double ToDouble()
		{
			return NativeImpl.toFloat64(Bits);
		}

		public static Decimal64 FromDecimal(Decimal value)
		{
			return new Decimal64(DotNetImpl.FromDecimal(value));
		}

		public Decimal ToDecimal()
		{
			return DotNetImpl.ToDecimal(ToUnderlying());
		}

		#endregion

		#region Conversion(Explicit operators)

		public static explicit operator Decimal64(long value)
		{
			return FromLong(value);
		}

		public static explicit operator long(Decimal64 decimal64)
		{
			return decimal64.ToLong();
		}

		public static explicit operator Decimal64(ulong value)
		{
			return FromULong(value);
		}

		public static explicit operator ulong(Decimal64 decimal64)
		{
			return decimal64.ToULong();
		}

		public static explicit operator Decimal64(int value)
		{
			return FromInt(value);
		}

		public static explicit operator int(Decimal64 decimal64)
		{
			return decimal64.ToInt();
		}

		public static explicit operator Decimal64(uint value)
		{
			return FromUInt(value);
		}

		public static explicit operator uint(Decimal64 decimal64)
		{
			return decimal64.ToUInt();
		}

		public static explicit operator Decimal64(short value)
		{
			return FromInt(value);
		}

		public static explicit operator short(Decimal64 decimal64)
		{
			return decimal64.ToShort();
		}

		public static explicit operator Decimal64(ushort value)
		{
			return FromUInt(value);
		}

		public static explicit operator ushort(Decimal64 decimal64)
		{
			return decimal64.ToUShort();
		}

		public static explicit operator Decimal64(sbyte value)
		{
			return FromInt(value);
		}

		public static explicit operator sbyte(Decimal64 decimal64)
		{
			return decimal64.ToSByte();
		}

		public static explicit operator Decimal64(byte value)
		{
			return FromUInt(value);
		}

		public static explicit operator byte(Decimal64 decimal64)
		{
			return decimal64.ToByte();
		}

		public static explicit operator Decimal64(Double value)
		{
			return FromDouble(value);
		}

		public static explicit operator double(Decimal64 decimal64)
		{
			return decimal64.ToDouble();
		}

		public static explicit operator Decimal(Decimal64 decimal64)
		{
			return decimal64.ToDecimal();
		}

		public static explicit operator Decimal64(Decimal value)
		{
			return FromDecimal(value);
		}

		#endregion

		#region Classification

		public Boolean IsNull()
		{
			return DotNetImpl.IsNull(Bits);
		}

		public Boolean IsNaN()
		{
			return DotNetImpl.IsNaN(Bits);
		}

		public Boolean IsInfinity()
		{
			return DotNetImpl.IsInfinity(Bits);
		}

		public Boolean IsPositiveInfinity()
		{
			return DotNetImpl.IsPositiveInfinity(Bits);
		}

		public Boolean IsNegativeInfinity()
		{
			return DotNetImpl.IsNegativeInfinity(Bits);
		}

		[Obsolete("IsSigned is deprecated, please use IsNegative instead for actual comparison with 0")]
		public Boolean IsSigned()
		{
			return DotNetImpl.SignBit(Bits);
		}

		public Boolean IsFinite()
		{
			return DotNetImpl.IsFinite(Bits);
		}

		public Boolean IsNormal()
		{
			return NativeImpl.isNormal(Bits);
		}

		#endregion

		#region Comparison

		public Boolean IsEqual(Decimal64 that)
		{
			UInt64 aBits = Bits, bBits = that.Bits;
			return aBits == bBits || NativeImpl.isEqual(aBits, bBits);
		}

		public static Boolean operator ==(Decimal64 a, Decimal64 b)
		{
			return a.IsEqual(b);
		}

		public Boolean IsIdentical(Decimal64 that)
		{
			return Bits == that.Bits;
		}

		public Boolean IsNotEqual(Decimal64 that)
		{
			UInt64 aBits = Bits, bBits = that.Bits;
			return aBits != bBits && NativeImpl.isNotEqual(aBits, bBits);
		}

		public static Boolean operator !=(Decimal64 a, Decimal64 b)
		{
			return a.IsNotEqual(b);
		}

		public Boolean IsGreater(Decimal64 that)
		{
			return NativeImpl.isGreater(Bits, that.Bits);
		}

		public static Boolean operator >(Decimal64 a, Decimal64 b)
		{
			return NativeImpl.isGreater(a.Bits, b.Bits);
		}

		public Boolean IsLess(Decimal64 that)
		{
			return NativeImpl.isLess(Bits, that.Bits);
		}

		public static Boolean operator <(Decimal64 a, Decimal64 b)
		{
			return NativeImpl.isLess(a.Bits, b.Bits);
		}

		public Boolean IsGreaterOrEqual(Decimal64 that)
		{
			return NativeImpl.isGreaterOrEqual(Bits, that.Bits);
		}

		public static Boolean operator >=(Decimal64 a, Decimal64 b)
		{
			return NativeImpl.isGreaterOrEqual(a.Bits, b.Bits);
		}

		public Boolean IsLessOrEqual(Decimal64 that)
		{
			return NativeImpl.isLessOrEqual(Bits, that.Bits);
		}

		public static Boolean operator <=(Decimal64 a, Decimal64 b)
		{
			return NativeImpl.isLessOrEqual(a.Bits, b.Bits);
		}

		public Boolean IsZero()
		{
			return DotNetImpl.IsZero(Bits);
		}

		public Boolean IsNonZero()
		{
			return !DotNetImpl.IsZero(Bits);
		}

		public Boolean IsPositive()
		{
			return DotNetImpl.IsPositive(Bits);
		}

		public Boolean IsNegative()
		{
			return DotNetImpl.IsNegative(Bits);
		}

		public Boolean IsNonPositive()
		{
			return DotNetImpl.IsNonPositive(Bits);
		}

		public Boolean IsNonNegative()
		{
			return DotNetImpl.IsNonNegative(Bits);
		}

		#endregion

		#region Minimum & Maximum

		public static Decimal64 Max(Decimal64 a, Decimal64 b)
		{
			return new Decimal64(NativeImpl.max2(a.Bits, b.Bits));
		}

		public static Decimal64 Max(Decimal64 a, Decimal64 b, Decimal64 c)
		{
			return new Decimal64(NativeImpl.max3(a.Bits, b.Bits, c.Bits));
		}

		public static Decimal64 Max(Decimal64 a, Decimal64 b, Decimal64 c, Decimal64 d)
		{
			return new Decimal64(NativeImpl.max4(a.Bits, b.Bits, c.Bits, d.Bits));
		}

		public static Decimal64 Min(Decimal64 a, Decimal64 b)
		{
			return new Decimal64(NativeImpl.min2(a.Bits, b.Bits));
		}

		public static Decimal64 Min(Decimal64 a, Decimal64 b, Decimal64 c)
		{
			return new Decimal64(NativeImpl.min3(a.Bits, b.Bits, c.Bits));
		}

		public static Decimal64 Min(Decimal64 a, Decimal64 b, Decimal64 c, Decimal64 d)
		{
			return new Decimal64(NativeImpl.min4(a.Bits, b.Bits, c.Bits, d.Bits));
		}

		public Decimal64 Max(Decimal64 b)
		{
			return new Decimal64(NativeImpl.max2(Bits, b.Bits));
		}

		public Decimal64 Min(Decimal64 b)
		{
			return new Decimal64(NativeImpl.min2(Bits, b.Bits));
		}

		#endregion

		#region Arithmetic

		public static Decimal64 operator +(Decimal64 value)
		{
			return value;
		}

		public Decimal64 Negate()
		{
			return new Decimal64(DotNetImpl.Negate(Bits));
		}

		public static Decimal64 operator -(Decimal64 value)
		{
			return new Decimal64(DotNetImpl.Negate(value.Bits));
		}

		public Decimal64 Abs()
		{
			return new Decimal64(DotNetImpl.Abs(Bits));
		}

		public Decimal64 Add(Decimal64 b)
		{
			return new Decimal64(NativeImpl.add2(Bits, b.Bits));
		}

		public Decimal64 Add(Decimal64 b, Decimal64 c)
		{
			return new Decimal64(NativeImpl.add3(Bits, b.Bits, c.Bits));
		}

		public Decimal64 Add(Decimal64 b, Decimal64 c, Decimal64 d)
		{
			return new Decimal64(NativeImpl.add4(Bits, b.Bits, c.Bits, d.Bits));
		}

		public static Decimal64 operator +(Decimal64 a, Decimal64 b)
		{
			return new Decimal64(NativeImpl.add2(a.Bits, b.Bits));
		}

		public Decimal64 Subtract(Decimal64 b)
		{
			return new Decimal64(NativeImpl.subtract(Bits, b.Bits));
		}

		public static Decimal64 operator -(Decimal64 a, Decimal64 b)
		{
			return new Decimal64(NativeImpl.subtract(a.Bits, b.Bits));
		}

		public Decimal64 Multiply(Decimal64 b)
		{
			return new Decimal64(NativeImpl.multiply2(Bits, b.Bits));
		}

		public Decimal64 Multiply(Decimal64 b, Decimal64 c)
		{
			return new Decimal64(NativeImpl.multiply3(Bits, b.Bits, c.Bits));
		}

		public Decimal64 Multiply(Decimal64 b, Decimal64 c, Decimal64 d)
		{
			return new Decimal64(NativeImpl.multiply4(Bits, b.Bits, c.Bits, d.Bits));
		}

		public static Decimal64 operator *(Decimal64 a, Decimal64 b)
		{
			return new Decimal64(NativeImpl.multiply2(a.Bits, b.Bits));
		}

		public Decimal64 MultiplyByInteger(Int32 b)
		{
			return new Decimal64(NativeImpl.multiplyByInt32(Bits, b));
		}

		public Decimal64 MultiplyByInteger(Int64 b)
		{
			return new Decimal64(NativeImpl.multiplyByInt64(Bits, b));
		}

		public static Decimal64 operator *(Decimal64 a, Int32 b)
		{
			return new Decimal64(NativeImpl.multiplyByInt32(a.Bits, b));
		}

		public static Decimal64 operator *(Int32 a, Decimal64 b)
		{
			return new Decimal64(NativeImpl.multiplyByInt32(b.Bits, a));
		}

		public static Decimal64 operator *(Decimal64 a, Int64 b)
		{
			return new Decimal64(NativeImpl.multiplyByInt64(a.Bits, b));
		}

		public static Decimal64 operator *(Int64 a, Decimal64 b)
		{
			return new Decimal64(NativeImpl.multiplyByInt64(b.Bits, a));
		}

		public Decimal64 Divide(Decimal64 b)
		{
			return new Decimal64(NativeImpl.divide(Bits, b.Bits));
		}

		public static Decimal64 operator /(Decimal64 a, Decimal64 b)
		{
			return new Decimal64(NativeImpl.divide(a.Bits, b.Bits));
		}

		public Decimal64 DivideByInteger(Int32 b)
		{
			return new Decimal64(NativeImpl.divideByInt32(Bits, b));
		}

		public Decimal64 DivideByInteger(Int64 b)
		{
			return new Decimal64(NativeImpl.divideByInt64(Bits, b));
		}

		public static Decimal64 operator /(Decimal64 a, Int32 b)
		{
			return new Decimal64(NativeImpl.divideByInt32(a.Bits, b));
		}

		public static Decimal64 operator /(Decimal64 a, Int64 b)
		{
			return new Decimal64(NativeImpl.divideByInt64(a.Bits, b));
		}

		public Decimal64 MultiplyAndAdd(Decimal64 b, Decimal64 c)
		{
			return new Decimal64(NativeImpl.multiplyAndAdd(Bits, b.Bits, c.Bits));
		}

		public Decimal64 ScaleByPowerOfTen(Int32 n)
		{
			return new Decimal64(NativeImpl.scaleByPowerOfTen(Bits, n));
		}

		public Decimal64 Mean(Decimal64 b)
		{
			return new Decimal64(NativeImpl.mean2(Bits, b.Bits));
		}

		#endregion

		#region Rounding

		public Decimal64 Ceiling()
		{
			return new Decimal64(NativeImpl.roundTowardsPositiveInfinity(Bits));
		}

		public Decimal64 RoundTowardsPositiveInfinity()
		{
			return new Decimal64(NativeImpl.roundTowardsPositiveInfinity(Bits));
		}

		public Decimal64 Floor()
		{
			return new Decimal64(NativeImpl.roundTowardsNegativeInfinity(Bits));
		}

		public Decimal64 RoundTowardsNegativeInfinity()
		{
			return new Decimal64(NativeImpl.roundTowardsNegativeInfinity(Bits));
		}

		public Decimal64 RoundTowardsZero()
		{
			return new Decimal64(NativeImpl.roundTowardsZero(Bits));
		}

		/// Identical to RoundToNearestTiesAwayFromZero
		public Decimal64 Round()
		{
			return new Decimal64(NativeImpl.roundToNearestTiesAwayFromZero(Bits));
		}

		public Decimal64 RoundToNearestTiesAwayFromZero()
		{
			return new Decimal64(NativeImpl.roundToNearestTiesAwayFromZero(Bits));
		}

		public Decimal64 RoundTowardsPositiveInfinity(Decimal64 multiple)
		{
			if (!multiple.IsFinite() || multiple.IsNonPositive())
				throw new ArgumentException("Multiple must be a positive finite number.");
			if (IsNaN())
				return this;

			UInt64 ratio = NativeImpl.roundTowardsPositiveInfinity(NativeImpl.divide(Bits, multiple.Bits));
			return new Decimal64(NativeImpl.multiply2(ratio, multiple.Bits));
		}

		public Decimal64 RoundTowardsNegativeInfinity(Decimal64 multiple)
		{
			if (!multiple.IsFinite() || multiple.IsNonPositive())
				throw new ArgumentException("Multiple must be a positive finite number.");
			if (IsNaN())
				return this;

			UInt64 ratio = NativeImpl.roundTowardsNegativeInfinity(NativeImpl.divide(Bits, multiple.Bits));
			return new Decimal64(NativeImpl.multiply2(ratio, multiple.Bits));
		}

		public Decimal64 RoundToNearestTiesAwayFromZero(Decimal64 multiple)
		{
			if (!multiple.IsFinite() || multiple.IsNonPositive())
				throw new ArgumentException("Multiple must be a positive finite number.");
			if (IsNaN())
				return this;

			UInt64 ratio = NativeImpl.roundToNearestTiesAwayFromZero(NativeImpl.divide(Bits, multiple.Bits));
			return new Decimal64(NativeImpl.multiply2(ratio, multiple.Bits));
		}

		#endregion

		#region Special

		public Decimal64 NextUp()
		{
			return new Decimal64(NativeImpl.nextUp(Bits));
		}

		public Decimal64 NextDown()
		{
			return new Decimal64(NativeImpl.nextDown(Bits));
		}

		/// <summary>
		/// Returns canonical representation of Decimal.
		/// We consider that all binary representations of one arithmetic value have the same canonical binary representation.
		/// Canonical representation of zeros = <see cref="Zero"/>>
		/// Canonical representation of NaNs = <see cref="NaN"/>
		/// Canonical representation of PositiveInfinities = <see cref="PositiveInfinity"/>
		/// Canonical representation of NegativeInfinities = <see cref="NegativeInfinity"/>
		/// </summary>
		/// <returns>Canonical representation of decimal argument.</returns>
		public Decimal64 Canonize()
		{
			if (IsNaN())
			{
				return NaN;
			}
			if (IsInfinity())
			{
				if (IsPositiveInfinity())
				{
					return PositiveInfinity;
				}
				else
				{
					return NegativeInfinity;
				}
			}
			return new Decimal64(DotNetImpl.Canonize(this.Bits));
		}


		#endregion

		#region Formatting & Parsing

		public static Decimal64 Parse(String text)
		{
			return FromDecimalDouble(Double.Parse(text, NumberStyles.Float, CultureInfo.InvariantCulture));
		}

		public static Boolean TryParse(String text, out Decimal64 result)
		{
			Double value;
			if (!Double.TryParse(text, NumberStyles.Float, CultureInfo.InvariantCulture, out value))
			{
				result = NaN;
				return false;
			}

			result = FromDecimalDouble(value);
			return true;
		}

		public StringBuilder AppendTo(StringBuilder text)
		{
			text.Append(ToDouble());
			return text;
		}

		#endregion

		#region IComparable<> Interface implementation
		public Int32 CompareTo(Decimal64 other)
		{
			return NativeImpl.compare(Bits, other.Bits);
		}

		#endregion

		#region IEquatable<> Interface implementation
		public Boolean Equals(Decimal64 other)
		{
			return Canonize().Bits == other.Canonize().Bits;
		}

		#endregion

		#region ISerializable Interface implementation

		public Decimal64(SerializationInfo info, StreamingContext context)
		{
			Bits = info.GetUInt64("");
		}

		public void GetObjectData(SerializationInfo info, StreamingContext context)
		{
			info.AddValue("", Bits);
		}

		#endregion
	}
}
