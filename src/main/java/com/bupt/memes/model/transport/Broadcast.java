// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: broadcast.proto

// Protobuf Java Version: 3.25.1
package com.bupt.memes.model.transport;

public final class Broadcast {
	private Broadcast() {
	}

	public static void registerAllExtensions(
			com.google.protobuf.ExtensionRegistryLite registry) {
	}

	public static void registerAllExtensions(
			com.google.protobuf.ExtensionRegistry registry) {
		registerAllExtensions(
				(com.google.protobuf.ExtensionRegistryLite) registry);
	}

	static final com.google.protobuf.Descriptors.Descriptor internal_static_BroadcastMessage_descriptor;
	static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internal_static_BroadcastMessage_fieldAccessorTable;

	public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
		return descriptor;
	}

	private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
	static {
		java.lang.String[] descriptorData = {
				"\n\017broadcast.proto\"v\n\020BroadcastMessage\022\n\n" +
						"\002id\030\001 \001(\t\022\021\n\ttimestamp\030\002 \001(\003\022\022\n\nsourceNo" +
						"de\030\003 \001(\t\022!\n\toperation\030\004 \001(\0162\016.OperationT" +
						"ype\022\014\n\004data\030\005 \001(\014*&\n\rOperationType\022\n\n\006DE" +
						"LETE\020\000\022\t\n\005CACHE\020\001B\"\n\036com.bupt.memes.mode" +
						"l.transportP\001b\006proto3"
		};
		descriptor = com.google.protobuf.Descriptors.FileDescriptor
				.internalBuildGeneratedFileFrom(descriptorData,
						new com.google.protobuf.Descriptors.FileDescriptor[] {
						});
		internal_static_BroadcastMessage_descriptor = getDescriptor().getMessageTypes().get(0);
		internal_static_BroadcastMessage_fieldAccessorTable = new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
				internal_static_BroadcastMessage_descriptor,
				new java.lang.String[] { "Id", "Timestamp", "SourceNode", "Operation", "Data", });
	}

	// @@protoc_insertion_point(outer_class_scope)
}
