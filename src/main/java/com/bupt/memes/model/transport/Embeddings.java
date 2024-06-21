// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: embeddings.proto

// Protobuf Java Version: 3.25.1
package com.bupt.memes.model.transport;

/**
 * Protobuf type {@code Embeddings}
 */
public final class Embeddings extends
        com.google.protobuf.GeneratedMessageV3 implements
        // @@protoc_insertion_point(message_implements:Embeddings)
        EmbeddingsOrBuilder {
    private static final long serialVersionUID = 0L;

    // Use Embeddings.newBuilder() to construct.
    private Embeddings(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
        super(builder);
    }

    private Embeddings() {
        data_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    @SuppressWarnings({ "unused" })
    protected java.lang.Object newInstance(
            UnusedPrivateParameter unused) {
        return new Embeddings();
    }

    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
        return com.bupt.memes.model.transport.EmbeddingsOuterClass.internal_static_Embeddings_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return com.bupt.memes.model.transport.EmbeddingsOuterClass.internal_static_Embeddings_fieldAccessorTable
                .ensureFieldAccessorsInitialized(
                        com.bupt.memes.model.transport.Embeddings.class, com.bupt.memes.model.transport.Embeddings.Builder.class);
    }

    public static final int COUNT_FIELD_NUMBER = 1;
    private int count_ = 0;

    /**
     * <code>int32 count = 1;</code>
     * 
     * @return The count.
     */
    @java.lang.Override
    public int getCount() {
        return count_;
    }

    public static final int DATA_FIELD_NUMBER = 2;
    @SuppressWarnings("serial")
    private java.util.List<com.bupt.memes.model.transport.Embedding> data_;

    /**
     * <code>repeated .Embedding data = 2;</code>
     */
    @java.lang.Override
    public java.util.List<com.bupt.memes.model.transport.Embedding> getDataList() {
        return data_;
    }

    /**
     * <code>repeated .Embedding data = 2;</code>
     */
    @java.lang.Override
    public java.util.List<? extends com.bupt.memes.model.transport.EmbeddingOrBuilder> getDataOrBuilderList() {
        return data_;
    }

    /**
     * <code>repeated .Embedding data = 2;</code>
     */
    @java.lang.Override
    public int getDataCount() {
        return data_.size();
    }

    /**
     * <code>repeated .Embedding data = 2;</code>
     */
    @java.lang.Override
    public com.bupt.memes.model.transport.Embedding getData(int index) {
        return data_.get(index);
    }

    /**
     * <code>repeated .Embedding data = 2;</code>
     */
    @java.lang.Override
    public com.bupt.memes.model.transport.EmbeddingOrBuilder getDataOrBuilder(
            int index) {
        return data_.get(index);
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
        if (count_ != 0) {
            output.writeInt32(1, count_);
        }
        for (int i = 0; i < data_.size(); i++) {
            output.writeMessage(2, data_.get(i));
        }
        getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
        int size = memoizedSize;
        if (size != -1)
            return size;

        size = 0;
        if (count_ != 0) {
            size += com.google.protobuf.CodedOutputStream
                    .computeInt32Size(1, count_);
        }
        for (int i = 0; i < data_.size(); i++) {
            size += com.google.protobuf.CodedOutputStream
                    .computeMessageSize(2, data_.get(i));
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
        if (!(obj instanceof com.bupt.memes.model.transport.Embeddings)) {
            return super.equals(obj);
        }
        com.bupt.memes.model.transport.Embeddings other = (com.bupt.memes.model.transport.Embeddings) obj;

        if (getCount() != other.getCount())
            return false;
        if (!getDataList()
                .equals(other.getDataList()))
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
        hash = (37 * hash) + COUNT_FIELD_NUMBER;
        hash = (53 * hash) + getCount();
        if (getDataCount() > 0) {
            hash = (37 * hash) + DATA_FIELD_NUMBER;
            hash = (53 * hash) + getDataList().hashCode();
        }
        hash = (29 * hash) + getUnknownFields().hashCode();
        memoizedHashCode = hash;
        return hash;
    }

    public static com.bupt.memes.model.transport.Embeddings parseFrom(
            java.nio.ByteBuffer data)
            throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static com.bupt.memes.model.transport.Embeddings parseFrom(
            java.nio.ByteBuffer data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.bupt.memes.model.transport.Embeddings parseFrom(
            com.google.protobuf.ByteString data)
            throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static com.bupt.memes.model.transport.Embeddings parseFrom(
            com.google.protobuf.ByteString data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.bupt.memes.model.transport.Embeddings parseFrom(byte[] data)
            throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static com.bupt.memes.model.transport.Embeddings parseFrom(
            byte[] data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.bupt.memes.model.transport.Embeddings parseFrom(java.io.InputStream input)
            throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
                .parseWithIOException(PARSER, input);
    }

    public static com.bupt.memes.model.transport.Embeddings parseFrom(
            java.io.InputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
                .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static com.bupt.memes.model.transport.Embeddings parseDelimitedFrom(java.io.InputStream input)
            throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
                .parseDelimitedWithIOException(PARSER, input);
    }

    public static com.bupt.memes.model.transport.Embeddings parseDelimitedFrom(
            java.io.InputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
                .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static com.bupt.memes.model.transport.Embeddings parseFrom(
            com.google.protobuf.CodedInputStream input)
            throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
                .parseWithIOException(PARSER, input);
    }

    public static com.bupt.memes.model.transport.Embeddings parseFrom(
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

    public static Builder newBuilder(com.bupt.memes.model.transport.Embeddings prototype) {
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
     * Protobuf type {@code Embeddings}
     */
    public static final class Builder extends
            com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
            // @@protoc_insertion_point(builder_implements:Embeddings)
            com.bupt.memes.model.transport.EmbeddingsOrBuilder {
        public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
            return com.bupt.memes.model.transport.EmbeddingsOuterClass.internal_static_Embeddings_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return com.bupt.memes.model.transport.EmbeddingsOuterClass.internal_static_Embeddings_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            com.bupt.memes.model.transport.Embeddings.class, com.bupt.memes.model.transport.Embeddings.Builder.class);
        }

        // Construct using com.bupt.memes.model.transport.Embeddings.newBuilder()
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
            count_ = 0;
            if (dataBuilder_ == null) {
                data_ = java.util.Collections.emptyList();
            } else {
                data_ = null;
                dataBuilder_.clear();
            }
            bitField0_ = (bitField0_ & ~0x00000002);
            return this;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
            return com.bupt.memes.model.transport.EmbeddingsOuterClass.internal_static_Embeddings_descriptor;
        }

        @java.lang.Override
        public com.bupt.memes.model.transport.Embeddings getDefaultInstanceForType() {
            return com.bupt.memes.model.transport.Embeddings.getDefaultInstance();
        }

        @java.lang.Override
        public com.bupt.memes.model.transport.Embeddings build() {
            com.bupt.memes.model.transport.Embeddings result = buildPartial();
            if (!result.isInitialized()) {
                throw newUninitializedMessageException(result);
            }
            return result;
        }

        @java.lang.Override
        public com.bupt.memes.model.transport.Embeddings buildPartial() {
            com.bupt.memes.model.transport.Embeddings result = new com.bupt.memes.model.transport.Embeddings(this);
            buildPartialRepeatedFields(result);
            if (bitField0_ != 0) {
                buildPartial0(result);
            }
            onBuilt();
            return result;
        }

        private void buildPartialRepeatedFields(com.bupt.memes.model.transport.Embeddings result) {
            if (dataBuilder_ == null) {
                if (((bitField0_ & 0x00000002) != 0)) {
                    data_ = java.util.Collections.unmodifiableList(data_);
                    bitField0_ = (bitField0_ & ~0x00000002);
                }
                result.data_ = data_;
            } else {
                result.data_ = dataBuilder_.build();
            }
        }

        private void buildPartial0(com.bupt.memes.model.transport.Embeddings result) {
            int from_bitField0_ = bitField0_;
            if (((from_bitField0_ & 0x00000001) != 0)) {
                result.count_ = count_;
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
            if (other instanceof com.bupt.memes.model.transport.Embeddings) {
                return mergeFrom((com.bupt.memes.model.transport.Embeddings) other);
            } else {
                super.mergeFrom(other);
                return this;
            }
        }

        public Builder mergeFrom(com.bupt.memes.model.transport.Embeddings other) {
            if (other == com.bupt.memes.model.transport.Embeddings.getDefaultInstance())
                return this;
            if (other.getCount() != 0) {
                setCount(other.getCount());
            }
            if (dataBuilder_ == null) {
                if (!other.data_.isEmpty()) {
                    if (data_.isEmpty()) {
                        data_ = other.data_;
                        bitField0_ = (bitField0_ & ~0x00000002);
                    } else {
                        ensureDataIsMutable();
                        data_.addAll(other.data_);
                    }
                    onChanged();
                }
            } else {
                if (!other.data_.isEmpty()) {
                    if (dataBuilder_.isEmpty()) {
                        dataBuilder_.dispose();
                        dataBuilder_ = null;
                        data_ = other.data_;
                        bitField0_ = (bitField0_ & ~0x00000002);
                        dataBuilder_ = com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ? getDataFieldBuilder() : null;
                    } else {
                        dataBuilder_.addAllMessages(other.data_);
                    }
                }
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
                        case 8: {
                            count_ = input.readInt32();
                            bitField0_ |= 0x00000001;
                            break;
                        } // case 8
                        case 18: {
                            com.bupt.memes.model.transport.Embedding m = input.readMessage(
                                    com.bupt.memes.model.transport.Embedding.parser(),
                                    extensionRegistry);
                            if (dataBuilder_ == null) {
                                ensureDataIsMutable();
                                data_.add(m);
                            } else {
                                dataBuilder_.addMessage(m);
                            }
                            break;
                        } // case 18
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

        private int count_;

        /**
         * <code>int32 count = 1;</code>
         * 
         * @return The count.
         */
        @java.lang.Override
        public int getCount() {
            return count_;
        }

        /**
         * <code>int32 count = 1;</code>
         * 
         * @param value
         *            The count to set.
         * @return This builder for chaining.
         */
        public Builder setCount(int value) {

            count_ = value;
            bitField0_ |= 0x00000001;
            onChanged();
            return this;
        }

        /**
         * <code>int32 count = 1;</code>
         * 
         * @return This builder for chaining.
         */
        public Builder clearCount() {
            bitField0_ = (bitField0_ & ~0x00000001);
            count_ = 0;
            onChanged();
            return this;
        }

        private java.util.List<com.bupt.memes.model.transport.Embedding> data_ = java.util.Collections.emptyList();

        private void ensureDataIsMutable() {
            if (!((bitField0_ & 0x00000002) != 0)) {
                data_ = new java.util.ArrayList<com.bupt.memes.model.transport.Embedding>(data_);
                bitField0_ |= 0x00000002;
            }
        }

        private com.google.protobuf.RepeatedFieldBuilderV3<com.bupt.memes.model.transport.Embedding, com.bupt.memes.model.transport.Embedding.Builder, com.bupt.memes.model.transport.EmbeddingOrBuilder> dataBuilder_;

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public java.util.List<com.bupt.memes.model.transport.Embedding> getDataList() {
            if (dataBuilder_ == null) {
                return java.util.Collections.unmodifiableList(data_);
            } else {
                return dataBuilder_.getMessageList();
            }
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public int getDataCount() {
            if (dataBuilder_ == null) {
                return data_.size();
            } else {
                return dataBuilder_.getCount();
            }
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public com.bupt.memes.model.transport.Embedding getData(int index) {
            if (dataBuilder_ == null) {
                return data_.get(index);
            } else {
                return dataBuilder_.getMessage(index);
            }
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public Builder setData(
                int index, com.bupt.memes.model.transport.Embedding value) {
            if (dataBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureDataIsMutable();
                data_.set(index, value);
                onChanged();
            } else {
                dataBuilder_.setMessage(index, value);
            }
            return this;
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public Builder setData(
                int index, com.bupt.memes.model.transport.Embedding.Builder builderForValue) {
            if (dataBuilder_ == null) {
                ensureDataIsMutable();
                data_.set(index, builderForValue.build());
                onChanged();
            } else {
                dataBuilder_.setMessage(index, builderForValue.build());
            }
            return this;
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public Builder addData(com.bupt.memes.model.transport.Embedding value) {
            if (dataBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureDataIsMutable();
                data_.add(value);
                onChanged();
            } else {
                dataBuilder_.addMessage(value);
            }
            return this;
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public Builder addData(
                int index, com.bupt.memes.model.transport.Embedding value) {
            if (dataBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureDataIsMutable();
                data_.add(index, value);
                onChanged();
            } else {
                dataBuilder_.addMessage(index, value);
            }
            return this;
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public Builder addData(
                com.bupt.memes.model.transport.Embedding.Builder builderForValue) {
            if (dataBuilder_ == null) {
                ensureDataIsMutable();
                data_.add(builderForValue.build());
                onChanged();
            } else {
                dataBuilder_.addMessage(builderForValue.build());
            }
            return this;
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public Builder addData(
                int index, com.bupt.memes.model.transport.Embedding.Builder builderForValue) {
            if (dataBuilder_ == null) {
                ensureDataIsMutable();
                data_.add(index, builderForValue.build());
                onChanged();
            } else {
                dataBuilder_.addMessage(index, builderForValue.build());
            }
            return this;
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public Builder addAllData(
                java.lang.Iterable<? extends com.bupt.memes.model.transport.Embedding> values) {
            if (dataBuilder_ == null) {
                ensureDataIsMutable();
                com.google.protobuf.AbstractMessageLite.Builder.addAll(
                        values, data_);
                onChanged();
            } else {
                dataBuilder_.addAllMessages(values);
            }
            return this;
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public Builder clearData() {
            if (dataBuilder_ == null) {
                data_ = java.util.Collections.emptyList();
                bitField0_ = (bitField0_ & ~0x00000002);
                onChanged();
            } else {
                dataBuilder_.clear();
            }
            return this;
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public Builder removeData(int index) {
            if (dataBuilder_ == null) {
                ensureDataIsMutable();
                data_.remove(index);
                onChanged();
            } else {
                dataBuilder_.remove(index);
            }
            return this;
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public com.bupt.memes.model.transport.Embedding.Builder getDataBuilder(
                int index) {
            return getDataFieldBuilder().getBuilder(index);
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public com.bupt.memes.model.transport.EmbeddingOrBuilder getDataOrBuilder(
                int index) {
            if (dataBuilder_ == null) {
                return data_.get(index);
            } else {
                return dataBuilder_.getMessageOrBuilder(index);
            }
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public java.util.List<? extends com.bupt.memes.model.transport.EmbeddingOrBuilder> getDataOrBuilderList() {
            if (dataBuilder_ != null) {
                return dataBuilder_.getMessageOrBuilderList();
            } else {
                return java.util.Collections.unmodifiableList(data_);
            }
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public com.bupt.memes.model.transport.Embedding.Builder addDataBuilder() {
            return getDataFieldBuilder().addBuilder(
                    com.bupt.memes.model.transport.Embedding.getDefaultInstance());
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public com.bupt.memes.model.transport.Embedding.Builder addDataBuilder(
                int index) {
            return getDataFieldBuilder().addBuilder(
                    index, com.bupt.memes.model.transport.Embedding.getDefaultInstance());
        }

        /**
         * <code>repeated .Embedding data = 2;</code>
         */
        public java.util.List<com.bupt.memes.model.transport.Embedding.Builder> getDataBuilderList() {
            return getDataFieldBuilder().getBuilderList();
        }

        private com.google.protobuf.RepeatedFieldBuilderV3<com.bupt.memes.model.transport.Embedding, com.bupt.memes.model.transport.Embedding.Builder, com.bupt.memes.model.transport.EmbeddingOrBuilder> getDataFieldBuilder() {
            if (dataBuilder_ == null) {
                dataBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<com.bupt.memes.model.transport.Embedding, com.bupt.memes.model.transport.Embedding.Builder, com.bupt.memes.model.transport.EmbeddingOrBuilder>(
                        data_,
                        ((bitField0_ & 0x00000002) != 0),
                        getParentForChildren(),
                        isClean());
                data_ = null;
            }
            return dataBuilder_;
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

        // @@protoc_insertion_point(builder_scope:Embeddings)
    }

    // @@protoc_insertion_point(class_scope:Embeddings)
    private static final com.bupt.memes.model.transport.Embeddings DEFAULT_INSTANCE;
    static {
        DEFAULT_INSTANCE = new com.bupt.memes.model.transport.Embeddings();
    }

    public static com.bupt.memes.model.transport.Embeddings getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Embeddings> PARSER = new com.google.protobuf.AbstractParser<Embeddings>() {
        @java.lang.Override
        public Embeddings parsePartialFrom(
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

    public static com.google.protobuf.Parser<Embeddings> parser() {
        return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Embeddings> getParserForType() {
        return PARSER;
    }

    @java.lang.Override
    public com.bupt.memes.model.transport.Embeddings getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

}
