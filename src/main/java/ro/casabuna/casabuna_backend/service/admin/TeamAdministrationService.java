package ro.casabuna.casabuna_backend.service.admin;

import org.springframework.data.web.PagedModel;
import ro.casabuna.casabuna_backend.model.dto.team.NewTeamRequestDto;
import ro.casabuna.casabuna_backend.model.dto.team.TeamResponseDto;
import ro.casabuna.casabuna_backend.model.query.PageQuery;

public interface TeamAdministrationService {

    TeamResponseDto create(NewTeamRequestDto newTeamRequestDto);

    PagedModel<TeamResponseDto> findAll(PageQuery pageQuery, String name);

}
