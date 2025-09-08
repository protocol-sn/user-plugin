package coop.stlma.tech.protocolsn.userplugin.endpoint;

import coop.stlma.tech.protocolsn.userplugin.SuccessResponse;
import coop.stlma.tech.protocolsn.userplugin.UserData;
import coop.stlma.tech.protocolsn.userplugin.UserReference;
import coop.stlma.tech.protocolsn.userplugin.UserSearchResponse;
import coop.stlma.tech.protocolsn.userplugin.UsersServiceGrpc;
import coop.stlma.tech.protocolsn.userplugin.service.UserService;
import coop.stlma.tech.protocolsn.userplugin.util.UserUtil;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;
import coop.stlma.tech.protocolsn.userplugin.UserQueryCriteria;

@GrpcService
@Slf4j
public class UserEndpoint extends UsersServiceGrpc.UsersServiceImplBase {

    private final UserService userService;

    public UserEndpoint(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void getUser(UserReference request, StreamObserver<UserData> responseObserver) {
        responseObserver.onNext(userService.getUser(UUID.fromString(request.getUserId()))
                .map(UserUtil::userDataFromPsnUser).block());
        responseObserver.onCompleted();
    }

    @Override
    public void approveUser(UserReference request, StreamObserver<SuccessResponse> responseObserver) {
        responseObserver.onNext(userService.approveUser(UUID.fromString(request.getUserId()))
                        .thenReturn(SuccessResponse.newBuilder().setSuccess(true).build())
                        .onErrorReturn(SuccessResponse.newBuilder().setSuccess(false).build())
                        .block());

        responseObserver.onCompleted();
    }

    @Override
    public void queryUsers(UserQueryCriteria request, StreamObserver<UserSearchResponse> responseObserver) {
        responseObserver.onNext(userService.queryUsers(request.getPagination().getOffset(),
                        request.getPagination().getLimit(),
                        request.hasApproved() ? request.getApproved() : null,
                        request.hasVerified() ? request.getVerified() : null,
                        request.hasRequestsVerification() ? request.getRequestsVerification() : null,
                        request.hasSearch() ? request.getSearch() : null)
                    .map(UserUtil::userDataFromPsnUser)
                .collectList()
                .map(userData -> UserSearchResponse.newBuilder()
                        .addAllUsers(userData)
                        .build())
                .block());

        responseObserver.onCompleted();
    }
}
