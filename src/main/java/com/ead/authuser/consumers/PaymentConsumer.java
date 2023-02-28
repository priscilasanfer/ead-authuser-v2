package com.ead.authuser.consumers;

import com.ead.authuser.dtos.PaymentEventDto;
import com.ead.authuser.enums.PaymentControl;
import com.ead.authuser.enums.RoleType;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.RoleService;
import com.ead.authuser.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class PaymentConsumer {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ead.broker.queue.paymentEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${ead.broker.exchange.paymentEvent}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true"))
    )
    public void listenPaymentEvent(@Payload PaymentEventDto paymentEventDto) {
        UserModel userModel = userService.findById(paymentEventDto.getUserId()).get();
        var roleMoel = roleService.findByRoleName(RoleType.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not Found."));

        switch (PaymentControl.valueOf(paymentEventDto.getPaymentControl())) {
            case EFFECTED:
                if (userModel.getUserType().equals(UserType.USER)) {
                    userModel.setUserType(UserType.STUDENT);
                    userModel.getRoles().add(roleMoel);
                    userService.updateUser(userModel);
                }
                break;
            case REFUSED:
                if (userModel.getUserType().equals(UserType.STUDENT)) {
                    userModel.setUserType(UserType.USER);
                    userModel.getRoles().remove(roleMoel);
                    userService.updateUser(userModel);
                }
                break;
            case ERROR:
                log.error("Payment with ERROR.");
        }
    }
}
