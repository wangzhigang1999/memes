// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: media.proto

// Protobuf Java Version: 3.25.1
package com.memes.model.transport;

public final class Media {
    private Media() {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions(
                (com.google.protobuf.ExtensionRegistryLite) registry);
    }

    static final com.google.protobuf.Descriptors.Descriptor internal_static_MediaMessage_descriptor;
    static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internal_static_MediaMessage_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
    static {
        java.lang.String[] descriptorData = {
                "\n\013media.proto\"Z\n\014MediaMessage\022\n\n\002id\030\001 \001(" +
                        "\t\022\021\n\ttimestamp\030\002 \001(\003\022\035\n\tmediaType\030\003 \001(\0162" +
                        "\n.MediaType\022\014\n\004data\030\004 \001(\014* \n\tMediaType\022\t" +
                        "\n\005IMAGE\020\000\022\010\n\004TEXT\020\001B\035\n\031com.memes.model.t" +
                        "ransportP\001b\006proto3"
        };
        descriptor = com.google.protobuf.Descriptors.FileDescriptor
                .internalBuildGeneratedFileFrom(descriptorData,
                        new com.google.protobuf.Descriptors.FileDescriptor[] {
                        });
        internal_static_MediaMessage_descriptor = getDescriptor().getMessageTypes().get(0);
        internal_static_MediaMessage_fieldAccessorTable = new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_MediaMessage_descriptor,
                new java.lang.String[] { "Id", "Timestamp", "MediaType", "Data", });
    }

    // @@protoc_insertion_point(outer_class_scope)
}