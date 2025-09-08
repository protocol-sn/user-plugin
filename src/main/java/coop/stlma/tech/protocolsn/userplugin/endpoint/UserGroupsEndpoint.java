package coop.stlma.tech.protocolsn.userplugin.endpoint;

import coop.stlma.tech.protocolsn.userplugin.GroupReference;
import coop.stlma.tech.protocolsn.userplugin.GroupSearchResponse;
import coop.stlma.tech.protocolsn.userplugin.SuccessResponse;
import coop.stlma.tech.protocolsn.userplugin.UserGroupQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.UserGroupReference;
import coop.stlma.tech.protocolsn.userplugin.UserGroupsServiceGrpc;
import coop.stlma.tech.protocolsn.userplugin.model.GroupQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.service.UserGroupsService;
import coop.stlma.tech.protocolsn.userplugin.util.GroupUtil;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@GrpcService
@Slf4j
public class UserGroupsEndpoint extends UserGroupsServiceGrpc.UserGroupsServiceImplBase {

    private final UserGroupsService userGroupsService;

    public UserGroupsEndpoint(UserGroupsService userGroupsService) {
        this.userGroupsService = userGroupsService;
    }

    @Override
    public void addUserToGroup(UserGroupReference request, StreamObserver<SuccessResponse> responseObserver) {
        responseObserver.onNext(userGroupsService.addUserToGroup(UUID.fromString(request.getUserId()),
                        UUID.fromString(request.getGroupId()))
                .thenReturn(SuccessResponse.newBuilder().setSuccess(true).build())
                .onErrorReturn(SuccessResponse.newBuilder().setSuccess(false).build())
                .block());

        responseObserver.onCompleted();
    }

    @Override
    public void removeUserFromGroup(UserGroupReference request, StreamObserver<SuccessResponse> responseObserver) {
        responseObserver.onNext(userGroupsService.removeUserFromGroup(UUID.fromString(request.getUserId()),
                        UUID.fromString(request.getGroupId()))
                .thenReturn(SuccessResponse.newBuilder().setSuccess(true).build())
                .onErrorReturn(SuccessResponse.newBuilder().setSuccess(false).build())
                .block());

        responseObserver.onCompleted();
    }

    @Override
    public void addGroupToDefaults(GroupReference request, StreamObserver<SuccessResponse> responseObserver) {
        responseObserver.onNext(userGroupsService.addGroupToDefaults(UUID.fromString(request.getGroupId()))
                .thenReturn(SuccessResponse.newBuilder().setSuccess(true).build())
                .onErrorReturn(SuccessResponse.newBuilder().setSuccess(false).build())
                .block());

        responseObserver.onCompleted();
    }

    @Override
    public void removeGroupFromDefaults(GroupReference request, StreamObserver<SuccessResponse> responseObserver) {
        responseObserver.onNext(userGroupsService.removeGroupFromDefault(UUID.fromString(request.getGroupId()))
                .thenReturn(SuccessResponse.newBuilder().setSuccess(true).build())
                .onErrorReturn(SuccessResponse.newBuilder().setSuccess(false).build())
                .block());

        responseObserver.onCompleted();
    }

    @Override
    public void getGroups(UserGroupQueryCriteria request, StreamObserver<GroupSearchResponse> responseObserver) {
        responseObserver.onNext(userGroupsService.queryGroups(GroupQueryCriteria.builder()
                        .offset(request.getPagination().getOffset())
                        .limit(request.getPagination().getLimit())
                        .defaultUserGroup(request.getDefaultUserGroup())
                .build())
                .map(GroupUtil::userGroupDataFromUserGroup)
                .collectList()
                .map(userGroupData -> GroupSearchResponse.newBuilder()
                        .addAllUserGroups(userGroupData)
                        .build())
                .block());

        responseObserver.onCompleted();
    }
}
