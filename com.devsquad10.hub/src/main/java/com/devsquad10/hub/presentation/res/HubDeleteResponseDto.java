package com.devsquad10.hub.presentation.res;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubDeleteResponseDto implements Serializable {
	private LocalDateTime deletedAt;
}
