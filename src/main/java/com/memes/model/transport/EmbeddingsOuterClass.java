// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: embeddings.proto

// Protobuf Java Version: 3.25.1
package com.memes.model.transport;

public final class EmbeddingsOuterClass {
    private EmbeddingsOuterClass() {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions(
                (com.google.protobuf.ExtensionRegistryLite) registry);
    }

    static final com.google.protobuf.Descriptors.Descriptor internal_static_Embeddings_descriptor;
    static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internal_static_Embeddings_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
    static {
        java.lang.String[] descriptorData = {
                "\n\020embeddings.proto\032\017embedding.proto\"5\n\nE" +
                        "mbeddings\022\r\n\005count\030\001 \001(\005\022\030\n\004data\030\002 \003(\0132\n" +
                        ".EmbeddingB\035\n\031com.memes.model.transportP" +
                        "\001b\006proto3"
        };
        descriptor = com.google.protobuf.Descriptors.FileDescriptor
                .internalBuildGeneratedFileFrom(descriptorData,
                        new com.google.protobuf.Descriptors.FileDescriptor[] {
                                com.memes.model.transport.EmbeddingOuterClass.getDescriptor(),
                        });
        internal_static_Embeddings_descriptor = getDescriptor().getMessageTypes().get(0);
        internal_static_Embeddings_fieldAccessorTable = new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_Embeddings_descriptor,
                new java.lang.String[] { "Count", "Data", });
        com.memes.model.transport.EmbeddingOuterClass.getDescriptor();
    }

    // @@protoc_insertion_point(outer_class_scope)
}
