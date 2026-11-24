package ro.casabuna.casabuna_backend.service.admin;

import lombok.AllArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.casabuna.casabuna_backend.model.dto.team.NewTeamRequestDto;
import ro.casabuna.casabuna_backend.model.dto.team.TeamResponseDto;
import ro.casabuna.casabuna_backend.model.entity.team.TeamEntity;
import ro.casabuna.casabuna_backend.model.mapper.team.TeamMapper;
import ro.casabuna.casabuna_backend.model.query.PageQuery;
import ro.casabuna.casabuna_backend.repository.TeamRepository;
import ro.casabuna.casabuna_backend.repository.specification.TeamSpecifications;

@Service
@AllArgsConstructor
public class TeamAdministrationServiceImpl implements TeamAdministrationService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Transactional
    @Override
    public TeamResponseDto create(NewTeamRequestDto newTeamRequestDto) {
        TeamEntity teamEntity = teamMapper.toEntity(newTeamRequestDto);
        return teamMapper.toDto(teamRepository.save(teamEntity));
    }

    @Override
    public PagedModel<TeamResponseDto> findAll(PageQuery pageQuery, String name) {
        var spec = TeamSpecifications.byName(name);
        var entitiesPage = teamRepository.findAll(spec, pageQuery.toPageRequest());
        return new PagedModel<>(entitiesPage.map(teamMapper::toDto));
    }
}
