package chans.usersideProcrastinationBot.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserInfoDto implements Serializable {
    @NotNull
    private UUID userUniqueId;

    private String userName;

    private String userPassword;

    private String mainProcessName;

    private long startProcessTime;
}
