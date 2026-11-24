package ro.casabuna.casabuna_backend.api.admin;

import lombok.AllArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import ro.casabuna.casabuna_backend.model.dto.team.NewTeamRequestDto;
import ro.casabuna.casabuna_backend.model.dto.team.TeamResponseDto;
import ro.casabuna.casabuna_backend.model.query.PageQuery;
import ro.casabuna.casabuna_backend.service.admin.TeamAdministrationService;

@RestController
@RequestMapping("/api/admin/team")
@AllArgsConstructor
public class TeamAdministrationController {

    private final TeamAdministrationService teamAdministrationService;

    @PostMapping
    public TeamResponseDto create(@RequestBody NewTeamRequestDto newTeamRequestDto) {
        return teamAdministrationService.create(newTeamRequestDto);
    }

    @GetMapping("/all")
    public PagedModel<TeamResponseDto> getAll(
            @ModelAttribute PageQuery pageQuery,
            @RequestParam(name = "name") String name
            ) {
        return teamAdministrationService.findAll(pageQuery, name);
    }

}
