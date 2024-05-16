package com.scholarsync.server.webSocket;

import com.scholarsync.server.types.NotificationType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CustomNotificationDTO {

    NotificationType notificationType;

    String groupId;

    String from;

    String to;

    String message = "fetch again";


}