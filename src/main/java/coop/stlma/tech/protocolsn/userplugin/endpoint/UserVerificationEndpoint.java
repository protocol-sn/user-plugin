package coop.stlma.tech.protocolsn.userplugin.endpoint;

import coop.stlma.tech.protocolsn.userplugin.SuccessResponse;
import coop.stlma.tech.protocolsn.userplugin.UserReference;
import coop.stlma.tech.protocolsn.userplugin.UserVerificationServiceGrpc;
import coop.stlma.tech.protocolsn.userplugin.service.UserService;
import coop.stlma.tech.protocolsn.userplugin.service.UserVerificationService;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@GrpcService
@Slf4j
public class UserVerificationEndpoint extends UserVerificationServiceGrpc.UserVerificationServiceImplBase {

    private final UserVerificationService userVerificationService;
    private final UserService userService;

    public UserVerificationEndpoint(UserVerificationService userVerificationService,
                                    UserService userService) {
        this.userVerificationService = userVerificationService;
        this.userService = userService;
    }

    @Override
    public void verifyUser(UserReference request, StreamObserver<SuccessResponse> responseObserver) {
        responseObserver.onNext(userVerificationService.verifyUser(UUID.fromString(request.getUserId()))
                .thenReturn(SuccessResponse.newBuilder().setSuccess(true).build())
                .onErrorReturn(SuccessResponse.newBuilder().setSuccess(false).build())
                .block());

        responseObserver.onCompleted();
    }

    @Override
    public void requestVerification(UserReference request, StreamObserver<SuccessResponse> responseObserver) {
        responseObserver.onNext(userVerificationService.requestVerification(UUID.fromString(request.getUserId()))
                .thenReturn(SuccessResponse.newBuilder().setSuccess(true).build())
                .onErrorReturn(SuccessResponse.newBuilder().setSuccess(false).build())
                .block());

        responseObserver.onCompleted();
    }
}
