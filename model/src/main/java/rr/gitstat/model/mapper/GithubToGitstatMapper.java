package rr.gitstat.model.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import rr.gitstat.model.entity.GitstatRepo;

public interface GithubToGitstatMapper {
	GithubToGitstatMapper INSTANCE = Mappers.getMapper( GithubToGitstatMapper.class );
	 
    @Mapping(source = "numberOfSeats", target = "seatCount")
    GitstatRepo mapGithubRepoToGitstatRepo(rr.gitstat.model.github.Repo repo); 
}
