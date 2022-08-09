package com.ground.domain.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.ground.domain.follow.repository.FollowRepository;
import com.ground.domain.jwt.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ground.domain.user.dto.UserFindPassDto;
import com.ground.domain.user.dto.UserLoginDto;
import com.ground.domain.user.dto.UserLoginResponseDto;
import com.ground.domain.user.dto.UserModifyPassDto;
import com.ground.domain.user.dto.UserProfileDto;
import com.ground.domain.user.dto.UserRegisterDto;
import com.ground.domain.user.dto.UserStateDto;
import com.ground.domain.user.dto.UserUpdateDto;
import com.ground.domain.user.entity.User;
import com.ground.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {
	
	@Autowired 
	private final UserRepository userRepository;
	@Autowired
	private final FollowRepository followRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Transactional
	//회원가입
	public boolean saveUser(UserRegisterDto params) {
		try {
			userRepository.save(params.toEntity());
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	@Transactional
	//아이디 중복 확인
	public boolean checkUsername(String username) {
		Optional<User> result = userRepository.findByUsername(username);
		if(result.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Transactional
	//닉네임 중복 확인
	public boolean checkNickname(String nickname) {
		Optional<User> result = userRepository.findByNickname(nickname);
		if(result.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Transactional
	//이메일 중복 확인
	public boolean checkEmail(String email) {
		Optional<User> result = userRepository.findByEmail(email);
		if(result.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Transactional
	//유저 계정 삭제 처리
	public boolean deleteUser(Long id) {
		User user = userRepository.findById(id).orElseThrow();
		try {
			user.deleteUser();
			return true;
		}
		catch(Exception e){
			return false;
		}
		
	}
	
	@Transactional
	//아이디 찾기
	public String findId(String email) {
		User user = userRepository.findByEmail(email).orElseThrow();
		return user.getUsername();
	}
	
	public boolean modifyPassCheck(UserFindPassDto params) {
		Optional<User> result = userRepository.findByEmailAndUsername(params.getEmail(), params.getUsername());
		if(result.isEmpty()) {
			return false;
		}
		else {
			return true;
		}
	}
	
	@Transactional
	//비밀번호 변경
	public boolean modifyPass(UserModifyPassDto params) {
		User user = userRepository.findByEmailAndUsername(params.getEmail(), params.getUsername()).orElseThrow(()
				-> new IllegalArgumentException("해당 유저는 존재하지 않습니다."));
		try {
			user.modifyPass(params.getPass());
			user.saveModDttm(LocalDateTime.now());
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	//토큰 생성 후 저장 로그인
	@Transactional
	public UserLoginResponseDto login(UserLoginDto params) {
		UserLoginResponseDto ulrd = new UserLoginResponseDto();
		try {
			Optional<User> user = userRepository.findByUsernameAndPass(params.getUsername(), params.getPass());
			if(user.isEmpty()) {
				ulrd.setResult("fail");
				return ulrd;
			}
			else {
				String ftoken = jwtTokenProvider.createToken(user.get().getUsername());
				user.get().saveFtoken(ftoken);
				ulrd.setResult("success");
				ulrd.setFtoken(ftoken);
				ulrd.setRegisterYN(user.get().isRegisterYN());
				return ulrd;
			}
		}
		catch(Exception e) {
			ulrd.setResult("fail");
			return ulrd;
		}
	}
	
	public UserStateDto userState(String ftoken) {
		String username = jwtTokenProvider.getSubject(ftoken);
		User user = userRepository.findByUsername(username).orElseThrow(IllegalArgumentException::new);
		UserStateDto userstate = new UserStateDto(user.getUsername(), user.getEmail(), user.getNickname(), 
	    		user.getFtoken(), user.getIntroduce(), user.getUserImage(), user.getGender(), user.getAge(), 
	    		user.isPrivateYN(), user.isRegisterYN());
		return userstate;
	}
	
	
	public boolean checkValidity(String ftoken) {
		boolean result = jwtTokenProvider.validateToken(ftoken);
		return result;
	}


	@Transactional
	public List<User> findFirstByUsernameLikeOrderByIdDesc(String username){
//		return userRepository.findFirstByUsernameLikeOrderByIdDesc(username);
		return userRepository.findAll();
	}
	
	
	@Transactional
	public User save(User user) {
		userRepository.save(user);
		
		return user;
	}

	// -----------------BSH-----------------
	// 프로필 조회
	@Transactional
    public UserProfileDto getUserProfile(Long id) {
		UserProfileDto userProfileDto = new UserProfileDto();

        User user = userRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("해당 유저는 존재하지 않습니다."));

		userProfileDto.setUser(user);
		userProfileDto.setUserFollowerCount(followRepository.findFollowerCountById(id));
		userProfileDto.setUserFollowingCount(followRepository.findFollowingCountById(id));

        return userProfileDto;
    }

	// 프로필 업데이트 페이지
    @Transactional
    public void getModifyUser(UserUpdateDto userUpdateDto) {

    }

	// 프로필 업데이트
	@Transactional
    public Long profileUpdate(Long id, UserUpdateDto entity) {
        User user = userRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("해당 유저는 존재하지 않습니다."));

        user.profileUpdate(entity, LocalDateTime.now());

        return id;
    }

}