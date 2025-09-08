package coop.stlma.tech.protocolsn.userplugin;

import io.grpc.ManagedChannel;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.grpc.annotation.GrpcChannel;
import io.micronaut.grpc.server.GrpcServerChannel;

@Factory
public class GrpcClients {

    @Bean
    UsersServiceGrpc.UsersServiceBlockingStub usersServiceBlockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) ManagedChannel channel) {
        return UsersServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    UserGroupsServiceGrpc.UserGroupsServiceBlockingStub userGroupsServiceBlockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) ManagedChannel channel) {
        return UserGroupsServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    UserVerificationServiceGrpc.UserVerificationServiceBlockingStub userVerificationServiceBlockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) ManagedChannel channel) {
        return UserVerificationServiceGrpc.newBlockingStub(channel);
    }
}
