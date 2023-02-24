/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package bcgov.rsbc.ride.kafka.models;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class payloadrecord extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 7155032878465139038L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"payloadrecord\",\"namespace\":\"bcgov.rsbc.ride.kafka.models\",\"fields\":[{\"name\":\"username\",\"type\":\"string\",\"doc\":\"\"},{\"name\":\"fname\",\"type\":\"string\",\"doc\":\"\"},{\"name\":\"lname\",\"type\":\"string\",\"doc\":\"\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<payloadrecord> ENCODER =
      new BinaryMessageEncoder<payloadrecord>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<payloadrecord> DECODER =
      new BinaryMessageDecoder<payloadrecord>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<payloadrecord> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<payloadrecord> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<payloadrecord> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<payloadrecord>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this payloadrecord to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a payloadrecord from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a payloadrecord instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static payloadrecord fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private CharSequence username;
  private CharSequence fname;
  private CharSequence lname;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public payloadrecord() {}

  /**
   * All-args constructor.
   * @param username The new value for username
   * @param fname The new value for fname
   * @param lname The new value for lname
   */
  public payloadrecord(CharSequence username, CharSequence fname, CharSequence lname) {
    this.username = username;
    this.fname = fname;
    this.lname = lname;
  }

  public SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public Object get(int field$) {
    switch (field$) {
    case 0: return username;
    case 1: return fname;
    case 2: return lname;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, Object value$) {
    switch (field$) {
    case 0: username = (CharSequence)value$; break;
    case 1: fname = (CharSequence)value$; break;
    case 2: lname = (CharSequence)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'username' field.
   * @return The value of the 'username' field.
   */
  public CharSequence getUsername() {
    return username;
  }


  /**
   * Sets the value of the 'username' field.
   * @param value the value to set.
   */
  public void setUsername(CharSequence value) {
    this.username = value;
  }

  /**
   * Gets the value of the 'fname' field.
   * @return The value of the 'fname' field.
   */
  public CharSequence getFname() {
    return fname;
  }


  /**
   * Sets the value of the 'fname' field.
   * @param value the value to set.
   */
  public void setFname(CharSequence value) {
    this.fname = value;
  }

  /**
   * Gets the value of the 'lname' field.
   * @return The value of the 'lname' field.
   */
  public CharSequence getLname() {
    return lname;
  }


  /**
   * Sets the value of the 'lname' field.
   * @param value the value to set.
   */
  public void setLname(CharSequence value) {
    this.lname = value;
  }

  /**
   * Creates a new payloadrecord RecordBuilder.
   * @return A new payloadrecord RecordBuilder
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Creates a new payloadrecord RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new payloadrecord RecordBuilder
   */
  public static Builder newBuilder(Builder other) {
    if (other == null) {
      return new Builder();
    } else {
      return new Builder(other);
    }
  }

  /**
   * Creates a new payloadrecord RecordBuilder by copying an existing payloadrecord instance.
   * @param other The existing instance to copy.
   * @return A new payloadrecord RecordBuilder
   */
  public static Builder newBuilder(payloadrecord other) {
    if (other == null) {
      return new Builder();
    } else {
      return new Builder(other);
    }
  }

  /**
   * RecordBuilder for payloadrecord instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<payloadrecord>
    implements org.apache.avro.data.RecordBuilder<payloadrecord> {

    private CharSequence username;
    private CharSequence fname;
    private CharSequence lname;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.username)) {
        this.username = data().deepCopy(fields()[0].schema(), other.username);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.fname)) {
        this.fname = data().deepCopy(fields()[1].schema(), other.fname);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.lname)) {
        this.lname = data().deepCopy(fields()[2].schema(), other.lname);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing payloadrecord instance
     * @param other The existing instance to copy.
     */
    private Builder(payloadrecord other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.username)) {
        this.username = data().deepCopy(fields()[0].schema(), other.username);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.fname)) {
        this.fname = data().deepCopy(fields()[1].schema(), other.fname);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.lname)) {
        this.lname = data().deepCopy(fields()[2].schema(), other.lname);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'username' field.
      * @return The value.
      */
    public CharSequence getUsername() {
      return username;
    }


    /**
      * Sets the value of the 'username' field.
      * @param value The value of 'username'.
      * @return This builder.
      */
    public Builder setUsername(CharSequence value) {
      validate(fields()[0], value);
      this.username = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'username' field has been set.
      * @return True if the 'username' field has been set, false otherwise.
      */
    public boolean hasUsername() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'username' field.
      * @return This builder.
      */
    public Builder clearUsername() {
      username = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'fname' field.
      * @return The value.
      */
    public CharSequence getFname() {
      return fname;
    }


    /**
      * Sets the value of the 'fname' field.
      * @param value The value of 'fname'.
      * @return This builder.
      */
    public Builder setFname(CharSequence value) {
      validate(fields()[1], value);
      this.fname = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'fname' field has been set.
      * @return True if the 'fname' field has been set, false otherwise.
      */
    public boolean hasFname() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'fname' field.
      * @return This builder.
      */
    public Builder clearFname() {
      fname = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'lname' field.
      * @return The value.
      */
    public CharSequence getLname() {
      return lname;
    }


    /**
      * Sets the value of the 'lname' field.
      * @param value The value of 'lname'.
      * @return This builder.
      */
    public Builder setLname(CharSequence value) {
      validate(fields()[2], value);
      this.lname = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'lname' field has been set.
      * @return True if the 'lname' field has been set, false otherwise.
      */
    public boolean hasLname() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'lname' field.
      * @return This builder.
      */
    public Builder clearLname() {
      lname = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public payloadrecord build() {
      try {
        payloadrecord record = new payloadrecord();
        record.username = fieldSetFlags()[0] ? this.username : (CharSequence) defaultValue(fields()[0]);
        record.fname = fieldSetFlags()[1] ? this.fname : (CharSequence) defaultValue(fields()[1]);
        record.lname = fieldSetFlags()[2] ? this.lname : (CharSequence) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<payloadrecord>
    WRITER$ = (org.apache.avro.io.DatumWriter<payloadrecord>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<payloadrecord>
    READER$ = (org.apache.avro.io.DatumReader<payloadrecord>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.username);

    out.writeString(this.fname);

    out.writeString(this.lname);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.username = in.readString(this.username instanceof Utf8 ? (Utf8)this.username : null);

      this.fname = in.readString(this.fname instanceof Utf8 ? (Utf8)this.fname : null);

      this.lname = in.readString(this.lname instanceof Utf8 ? (Utf8)this.lname : null);

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.username = in.readString(this.username instanceof Utf8 ? (Utf8)this.username : null);
          break;

        case 1:
          this.fname = in.readString(this.fname instanceof Utf8 ? (Utf8)this.fname : null);
          break;

        case 2:
          this.lname = in.readString(this.lname instanceof Utf8 ? (Utf8)this.lname : null);
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










