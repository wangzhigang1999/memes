// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: broadcast.proto

// Protobuf Java Version: 3.25.1
package com.bupt.memes.model.transport;

/**
 * Protobuf type {@code BroadcastMessage}
 */
public final class BroadcastMessage extends
        com.google.protobuf.GeneratedMessageV3 implements
        // @@protoc_insertion_point(message_implements:BroadcastMessage)
        BroadcastMessageOrBuilder {
    private static final long serialVersionUID = 0L;

    // Use BroadcastMessage.newBuilder() to construct.
    private BroadcastMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
        super(builder);
    }

    private BroadcastMessage() {
        id_ = "";
        sourceNode_ = "";
        operation_ = 0;
        data_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({ "unused" })
    protected java.lang.Object newInstance(
            UnusedPrivateParameter unused) {
        return new BroadcastMessage();
    }

    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
        return com.bupt.memes.model.transport.Broadcast.internal_static_BroadcastMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return com.bupt.memes.model.transport.Broadcast.internal_static_BroadcastMessage_fieldAccessorTable
                .ensureFieldAccessorsInitialized(
                        com.bupt.memes.model.transport.BroadcastMessage.class, com.bupt.memes.model.transport.BroadcastMessage.Builder.class);
    }

    public static final int ID_FIELD_NUMBER = 1;
    @SuppressWarnings("serial")
    private volatile java.lang.Object id_ = "";

    /**
     * <code>string id = 1;</code>
     * 
     * @return The id.
     */
    @java.lang.Override
    public java.lang.String getId() {
        java.lang.Object ref = id_;
        if (ref instanceof java.lang.String) {
            return (java.lang.String) ref;
        } else {
            com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
            java.lang.String s = bs.toStringUtf8();
            id_ = s;
            return s;
        }
    }

    /**
     * <code>string id = 1;</code>
     * 
     * @return The bytes for id.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getIdBytes() {
        java.lang.Object ref = id_;
        if (ref instanceof java.lang.String) {
            com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8(
                    (java.lang.String) ref);
            id_ = b;
            return b;
        } else {
            return (com.google.protobuf.ByteString) ref;
        }
    }

    public static final int TIMESTAMP_FIELD_NUMBER = 2;
    private long timestamp_ = 0L;

    /**
     * <code>int64 timestamp = 2;</code>
     * 
     * @return The timestamp.
     */
    @java.lang.Override
    public long getTimestamp() {
        return timestamp_;
    }

    public static final int SOURCENODE_FIELD_NUMBER = 3;
    @SuppressWarnings("serial")
    private volatile java.lang.Object sourceNode_ = "";

    /**
     * <code>string sourceNode = 3;</code>
     * 
     * @return The sourceNode.
     */
    @java.lang.Override
    public java.lang.String getSourceNode() {
        java.lang.Object ref = sourceNode_;
        if (ref instanceof java.lang.String) {
            return (java.lang.String) ref;
        } else {
            com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
            java.lang.String s = bs.toStringUtf8();
            sourceNode_ = s;
            return s;
        }
    }

    /**
     * <code>string sourceNode = 3;</code>
     * 
     * @return The bytes for sourceNode.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getSourceNodeBytes() {
        java.lang.Object ref = sourceNode_;
        if (ref instanceof java.lang.String) {
            com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8(
                    (java.lang.String) ref);
            sourceNode_ = b;
            return b;
        } else {
            return (com.google.protobuf.ByteString) ref;
        }
    }

    public static final int OPERATION_FIELD_NUMBER = 4;
    private int operation_ = 0;

    /**
     * <code>.OperationType operation = 4;</code>
     * 
     * @return The enum numeric value on the wire for operation.
     */
    @java.lang.Override
    public int getOperationValue() {
        return operation_;
    }

    /**
     * <code>.OperationType operation = 4;</code>
     * 
     * @return The operation.
     */
    @java.lang.Override
    public com.bupt.memes.model.transport.OperationType getOperation() {
        com.bupt.memes.model.transport.OperationType result = com.bupt.memes.model.transport.OperationType.forNumber(operation_);
        return result == null ? com.bupt.memes.model.transport.OperationType.UNRECOGNIZED : result;
    }

    public static final int DATA_FIELD_NUMBER = 5;
    private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;

    /**
     * <code>bytes data = 5;</code>
     * 
     * @return The data.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getData() {
        return data_;
    }

    private byte memoizedIsInitialized = -1;

    @java.lang.Override
    public final boolean isInitialized() {
        byte isInitialized = memoizedIsInitialized;
        if (isInitialized == 1)
            return true;
        if (isInitialized == 0)
            return false;

        memoizedIsInitialized = 1;
        return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
            throws java.io.IOException {
        if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(id_)) {
            com.google.protobuf.GeneratedMessageV3.writeString(output, 1, id_);
        }
        if (timestamp_ != 0L) {
            output.writeInt64(2, timestamp_);
        }
        if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(sourceNode_)) {
            com.google.protobuf.GeneratedMessageV3.writeString(output, 3, sourceNode_);
        }
        if (operation_ != com.bupt.memes.model.transport.OperationType.DELETE.getNumber()) {
            output.writeEnum(4, operation_);
        }
        if (!data_.isEmpty()) {
            output.writeBytes(5, data_);
        }
        getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
        int size = memoizedSize;
        if (size != -1)
            return size;

        size = 0;
        if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(id_)) {
            size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, id_);
        }
        if (timestamp_ != 0L) {
            size += com.google.protobuf.CodedOutputStream
                    .computeInt64Size(2, timestamp_);
        }
        if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(sourceNode_)) {
            size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, sourceNode_);
        }
        if (operation_ != com.bupt.memes.model.transport.OperationType.DELETE.getNumber()) {
            size += com.google.protobuf.CodedOutputStream
                    .computeEnumSize(4, operation_);
        }
        if (!data_.isEmpty()) {
            size += com.google.protobuf.CodedOutputStream
                    .computeBytesSize(5, data_);
        }
        size += getUnknownFields().getSerializedSize();
        memoizedSize = size;
        return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof com.bupt.memes.model.transport.BroadcastMessage)) {
            return super.equals(obj);
        }
        com.bupt.memes.model.transport.BroadcastMessage other = (com.bupt.memes.model.transport.BroadcastMessage) obj;

        if (!getId()
                .equals(other.getId()))
            return false;
        if (getTimestamp() != other.getTimestamp())
            return false;
        if (!getSourceNode()
                .equals(other.getSourceNode()))
            return false;
        if (operation_ != other.operation_)
            return false;
        if (!getData()
                .equals(other.getData()))
            return false;
        if (!getUnknownFields().equals(other.getUnknownFields()))
            return false;
        return true;
    }

    @java.lang.Override
    public int hashCode() {
        if (memoizedHashCode != 0) {
            return memoizedHashCode;
        }
        int hash = 41;
        hash = (19 * hash) + getDescriptor().hashCode();
        hash = (37 * hash) + ID_FIELD_NUMBER;
        hash = (53 * hash) + getId().hashCode();
        hash = (37 * hash) + TIMESTAMP_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
                getTimestamp());
        hash = (37 * hash) + SOURCENODE_FIELD_NUMBER;
        hash = (53 * hash) + getSourceNode().hashCode();
        hash = (37 * hash) + OPERATION_FIELD_NUMBER;
        hash = (53 * hash) + operation_;
        hash = (37 * hash) + DATA_FIELD_NUMBER;
        hash = (53 * hash) + getData().hashCode();
        hash = (29 * hash) + getUnknownFields().hashCode();
        memoizedHashCode = hash;
        return hash;
    }

    public static com.bupt.memes.model.transport.BroadcastMessage parseFrom(
            java.nio.ByteBuffer data)
            throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static com.bupt.memes.model.transport.BroadcastMessage parseFrom(
            java.nio.ByteBuffer data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.bupt.memes.model.transport.BroadcastMessage parseFrom(
            com.google.protobuf.ByteString data)
            throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static com.bupt.memes.model.transport.BroadcastMessage parseFrom(
            com.google.protobuf.ByteString data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.bupt.memes.model.transport.BroadcastMessage parseFrom(byte[] data)
            throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static com.bupt.memes.model.transport.BroadcastMessage parseFrom(
            byte[] data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.bupt.memes.model.transport.BroadcastMessage parseFrom(java.io.InputStream input)
            throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
                .parseWithIOException(PARSER, input);
    }

    public static com.bupt.memes.model.transport.BroadcastMessage parseFrom(
            java.io.InputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
                .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static com.bupt.memes.model.transport.BroadcastMessage parseDelimitedFrom(java.io.InputStream input)
            throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
                .parseDelimitedWithIOException(PARSER, input);
    }

    public static com.bupt.memes.model.transport.BroadcastMessage parseDelimitedFrom(
            java.io.InputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
                .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static com.bupt.memes.model.transport.BroadcastMessage parseFrom(
            com.google.protobuf.CodedInputStream input)
            throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
                .parseWithIOException(PARSER, input);
    }

    public static com.bupt.memes.model.transport.BroadcastMessage parseFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
                .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() {
        return newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(com.bupt.memes.model.transport.BroadcastMessage prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }

    @java.lang.Override
    public Builder toBuilder() {
        return this == DEFAULT_INSTANCE
                ? new Builder()
                : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
            com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        Builder builder = new Builder(parent);
        return builder;
    }

    /**
     * Protobuf type {@code BroadcastMessage}
     */
    public static final class Builder extends
            com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
            // @@protoc_insertion_point(builder_implements:BroadcastMessage)
            com.bupt.memes.model.transport.BroadcastMessageOrBuilder {
        public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
            return com.bupt.memes.model.transport.Broadcast.internal_static_BroadcastMessage_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return com.bupt.memes.model.transport.Broadcast.internal_static_BroadcastMessage_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            com.bupt.memes.model.transport.BroadcastMessage.class, com.bupt.memes.model.transport.BroadcastMessage.Builder.class);
        }

        // Construct using com.bupt.memes.model.transport.BroadcastMessage.newBuilder()
        private Builder() {

        }

        private Builder(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            super(parent);

        }

        @java.lang.Override
        public Builder clear() {
            super.clear();
            bitField0_ = 0;
            id_ = "";
            timestamp_ = 0L;
            sourceNode_ = "";
            operation_ = 0;
            data_ = com.google.protobuf.ByteString.EMPTY;
            return this;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
            return com.bupt.memes.model.transport.Broadcast.internal_static_BroadcastMessage_descriptor;
        }

        @java.lang.Override
        public com.bupt.memes.model.transport.BroadcastMessage getDefaultInstanceForType() {
            return com.bupt.memes.model.transport.BroadcastMessage.getDefaultInstance();
        }

        @java.lang.Override
        public com.bupt.memes.model.transport.BroadcastMessage build() {
            com.bupt.memes.model.transport.BroadcastMessage result = buildPartial();
            if (!result.isInitialized()) {
                throw newUninitializedMessageException(result);
            }
            return result;
        }

        @java.lang.Override
        public com.bupt.memes.model.transport.BroadcastMessage buildPartial() {
            com.bupt.memes.model.transport.BroadcastMessage result = new com.bupt.memes.model.transport.BroadcastMessage(this);
            if (bitField0_ != 0) {
                buildPartial0(result);
            }
            onBuilt();
            return result;
        }

        private void buildPartial0(com.bupt.memes.model.transport.BroadcastMessage result) {
            int from_bitField0_ = bitField0_;
            if (((from_bitField0_ & 0x00000001) != 0)) {
                result.id_ = id_;
            }
            if (((from_bitField0_ & 0x00000002) != 0)) {
                result.timestamp_ = timestamp_;
            }
            if (((from_bitField0_ & 0x00000004) != 0)) {
                result.sourceNode_ = sourceNode_;
            }
            if (((from_bitField0_ & 0x00000008) != 0)) {
                result.operation_ = operation_;
            }
            if (((from_bitField0_ & 0x00000010) != 0)) {
                result.data_ = data_;
            }
        }

        @java.lang.Override
        public Builder clone() {
            return super.clone();
        }

        @java.lang.Override
        public Builder setField(
                com.google.protobuf.Descriptors.FieldDescriptor field,
                java.lang.Object value) {
            return super.setField(field, value);
        }

        @java.lang.Override
        public Builder clearField(
                com.google.protobuf.Descriptors.FieldDescriptor field) {
            return super.clearField(field);
        }

        @java.lang.Override
        public Builder clearOneof(
                com.google.protobuf.Descriptors.OneofDescriptor oneof) {
            return super.clearOneof(oneof);
        }

        @java.lang.Override
        public Builder setRepeatedField(
                com.google.protobuf.Descriptors.FieldDescriptor field,
                int index, java.lang.Object value) {
            return super.setRepeatedField(field, index, value);
        }

        @java.lang.Override
        public Builder addRepeatedField(
                com.google.protobuf.Descriptors.FieldDescriptor field,
                java.lang.Object value) {
            return super.addRepeatedField(field, value);
        }

        @java.lang.Override
        public Builder mergeFrom(com.google.protobuf.Message other) {
            if (other instanceof com.bupt.memes.model.transport.BroadcastMessage) {
                return mergeFrom((com.bupt.memes.model.transport.BroadcastMessage) other);
            } else {
                super.mergeFrom(other);
                return this;
            }
        }

        public Builder mergeFrom(com.bupt.memes.model.transport.BroadcastMessage other) {
            if (other == com.bupt.memes.model.transport.BroadcastMessage.getDefaultInstance())
                return this;
            if (!other.getId().isEmpty()) {
                id_ = other.id_;
                bitField0_ |= 0x00000001;
                onChanged();
            }
            if (other.getTimestamp() != 0L) {
                setTimestamp(other.getTimestamp());
            }
            if (!other.getSourceNode().isEmpty()) {
                sourceNode_ = other.sourceNode_;
                bitField0_ |= 0x00000004;
                onChanged();
            }
            if (other.operation_ != 0) {
                setOperationValue(other.getOperationValue());
            }
            if (other.getData() != com.google.protobuf.ByteString.EMPTY) {
                setData(other.getData());
            }
            this.mergeUnknownFields(other.getUnknownFields());
            onChanged();
            return this;
        }

        @java.lang.Override
        public final boolean isInitialized() {
            return true;
        }

        @java.lang.Override
        public Builder mergeFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            if (extensionRegistry == null) {
                throw new java.lang.NullPointerException();
            }
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        case 10: {
                            id_ = input.readStringRequireUtf8();
                            bitField0_ |= 0x00000001;
                            break;
                        } // case 10
                        case 16: {
                            timestamp_ = input.readInt64();
                            bitField0_ |= 0x00000002;
                            break;
                        } // case 16
                        case 26: {
                            sourceNode_ = input.readStringRequireUtf8();
                            bitField0_ |= 0x00000004;
                            break;
                        } // case 26
                        case 32: {
                            operation_ = input.readEnum();
                            bitField0_ |= 0x00000008;
                            break;
                        } // case 32
                        case 42: {
                            data_ = input.readBytes();
                            bitField0_ |= 0x00000010;
                            break;
                        } // case 42
                        default: {
                            if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                                done = true; // was an endgroup tag
                            }
                            break;
                        } // default:
                    } // switch (tag)
                } // while (!done)
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.unwrapIOException();
            } finally {
                onChanged();
            } // finally
            return this;
        }

        private int bitField0_;

        private java.lang.Object id_ = "";

        /**
         * <code>string id = 1;</code>
         * 
         * @return The id.
         */
        public java.lang.String getId() {
            java.lang.Object ref = id_;
            if (!(ref instanceof java.lang.String)) {
                com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                id_ = s;
                return s;
            } else {
                return (java.lang.String) ref;
            }
        }

        /**
         * <code>string id = 1;</code>
         * 
         * @return The bytes for id.
         */
        public com.google.protobuf.ByteString getIdBytes() {
            java.lang.Object ref = id_;
            if (ref instanceof String) {
                com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8(
                        (java.lang.String) ref);
                id_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        /**
         * <code>string id = 1;</code>
         * 
         * @param value
         *            The id to set.
         * @return This builder for chaining.
         */
        public Builder setId(
                java.lang.String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            id_ = value;
            bitField0_ |= 0x00000001;
            onChanged();
            return this;
        }

        /**
         * <code>string id = 1;</code>
         * 
         * @return This builder for chaining.
         */
        public Builder clearId() {
            id_ = getDefaultInstance().getId();
            bitField0_ = (bitField0_ & ~0x00000001);
            onChanged();
            return this;
        }

        /**
         * <code>string id = 1;</code>
         * 
         * @param value
         *            The bytes for id to set.
         * @return This builder for chaining.
         */
        public Builder setIdBytes(
                com.google.protobuf.ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            checkByteStringIsUtf8(value);
            id_ = value;
            bitField0_ |= 0x00000001;
            onChanged();
            return this;
        }

        private long timestamp_;

        /**
         * <code>int64 timestamp = 2;</code>
         * 
         * @return The timestamp.
         */
        @java.lang.Override
        public long getTimestamp() {
            return timestamp_;
        }

        /**
         * <code>int64 timestamp = 2;</code>
         * 
         * @param value
         *            The timestamp to set.
         * @return This builder for chaining.
         */
        public Builder setTimestamp(long value) {

            timestamp_ = value;
            bitField0_ |= 0x00000002;
            onChanged();
            return this;
        }

        /**
         * <code>int64 timestamp = 2;</code>
         * 
         * @return This builder for chaining.
         */
        public Builder clearTimestamp() {
            bitField0_ = (bitField0_ & ~0x00000002);
            timestamp_ = 0L;
            onChanged();
            return this;
        }

        private java.lang.Object sourceNode_ = "";

        /**
         * <code>string sourceNode = 3;</code>
         * 
         * @return The sourceNode.
         */
        public java.lang.String getSourceNode() {
            java.lang.Object ref = sourceNode_;
            if (!(ref instanceof java.lang.String)) {
                com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                sourceNode_ = s;
                return s;
            } else {
                return (java.lang.String) ref;
            }
        }

        /**
         * <code>string sourceNode = 3;</code>
         * 
         * @return The bytes for sourceNode.
         */
        public com.google.protobuf.ByteString getSourceNodeBytes() {
            java.lang.Object ref = sourceNode_;
            if (ref instanceof String) {
                com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8(
                        (java.lang.String) ref);
                sourceNode_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        /**
         * <code>string sourceNode = 3;</code>
         * 
         * @param value
         *            The sourceNode to set.
         * @return This builder for chaining.
         */
        public Builder setSourceNode(
                java.lang.String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            sourceNode_ = value;
            bitField0_ |= 0x00000004;
            onChanged();
            return this;
        }

        /**
         * <code>string sourceNode = 3;</code>
         * 
         * @return This builder for chaining.
         */
        public Builder clearSourceNode() {
            sourceNode_ = getDefaultInstance().getSourceNode();
            bitField0_ = (bitField0_ & ~0x00000004);
            onChanged();
            return this;
        }

        /**
         * <code>string sourceNode = 3;</code>
         * 
         * @param value
         *            The bytes for sourceNode to set.
         * @return This builder for chaining.
         */
        public Builder setSourceNodeBytes(
                com.google.protobuf.ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            checkByteStringIsUtf8(value);
            sourceNode_ = value;
            bitField0_ |= 0x00000004;
            onChanged();
            return this;
        }

        private int operation_ = 0;

        /**
         * <code>.OperationType operation = 4;</code>
         * 
         * @return The enum numeric value on the wire for operation.
         */
        @java.lang.Override
        public int getOperationValue() {
            return operation_;
        }

        /**
         * <code>.OperationType operation = 4;</code>
         * 
         * @param value
         *            The enum numeric value on the wire for operation to set.
         * @return This builder for chaining.
         */
        public Builder setOperationValue(int value) {
            operation_ = value;
            bitField0_ |= 0x00000008;
            onChanged();
            return this;
        }

        /**
         * <code>.OperationType operation = 4;</code>
         * 
         * @return The operation.
         */
        @java.lang.Override
        public com.bupt.memes.model.transport.OperationType getOperation() {
            com.bupt.memes.model.transport.OperationType result = com.bupt.memes.model.transport.OperationType.forNumber(operation_);
            return result == null ? com.bupt.memes.model.transport.OperationType.UNRECOGNIZED : result;
        }

        /**
         * <code>.OperationType operation = 4;</code>
         * 
         * @param value
         *            The operation to set.
         * @return This builder for chaining.
         */
        public Builder setOperation(com.bupt.memes.model.transport.OperationType value) {
            if (value == null) {
                throw new NullPointerException();
            }
            bitField0_ |= 0x00000008;
            operation_ = value.getNumber();
            onChanged();
            return this;
        }

        /**
         * <code>.OperationType operation = 4;</code>
         * 
         * @return This builder for chaining.
         */
        public Builder clearOperation() {
            bitField0_ = (bitField0_ & ~0x00000008);
            operation_ = 0;
            onChanged();
            return this;
        }

        private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;

        /**
         * <code>bytes data = 5;</code>
         * 
         * @return The data.
         */
        @java.lang.Override
        public com.google.protobuf.ByteString getData() {
            return data_;
        }

        /**
         * <code>bytes data = 5;</code>
         * 
         * @param value
         *            The data to set.
         * @return This builder for chaining.
         */
        public Builder setData(com.google.protobuf.ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            data_ = value;
            bitField0_ |= 0x00000010;
            onChanged();
            return this;
        }

        /**
         * <code>bytes data = 5;</code>
         * 
         * @return This builder for chaining.
         */
        public Builder clearData() {
            bitField0_ = (bitField0_ & ~0x00000010);
            data_ = getDefaultInstance().getData();
            onChanged();
            return this;
        }

        @java.lang.Override
        public final Builder setUnknownFields(
                final com.google.protobuf.UnknownFieldSet unknownFields) {
            return super.setUnknownFields(unknownFields);
        }

        @java.lang.Override
        public final Builder mergeUnknownFields(
                final com.google.protobuf.UnknownFieldSet unknownFields) {
            return super.mergeUnknownFields(unknownFields);
        }

        // @@protoc_insertion_point(builder_scope:BroadcastMessage)
    }

    // @@protoc_insertion_point(class_scope:BroadcastMessage)
    private static final com.bupt.memes.model.transport.BroadcastMessage DEFAULT_INSTANCE;
    static {
        DEFAULT_INSTANCE = new com.bupt.memes.model.transport.BroadcastMessage();
    }

    public static com.bupt.memes.model.transport.BroadcastMessage getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<BroadcastMessage> PARSER = new com.google.protobuf.AbstractParser<BroadcastMessage>() {
        @java.lang.Override
        public BroadcastMessage parsePartialFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            Builder builder = newBuilder();
            try {
                builder.mergeFrom(input, extensionRegistry);
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(builder.buildPartial());
            } catch (com.google.protobuf.UninitializedMessageException e) {
                throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(e)
                        .setUnfinishedMessage(builder.buildPartial());
            }
            return builder.buildPartial();
        }
    };

    public static com.google.protobuf.Parser<BroadcastMessage> parser() {
        return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<BroadcastMessage> getParserForType() {
        return PARSER;
    }

    @java.lang.Override
    public com.bupt.memes.model.transport.BroadcastMessage getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

}
