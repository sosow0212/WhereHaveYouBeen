package hackathon.server.service.sign;

import hackathon.server.config.jwt.TokenProvider;
import hackathon.server.dto.sign.LoginRequestDto;
import hackathon.server.dto.sign.ReissueRequestDto;
import hackathon.server.dto.sign.SignupRequestDto;
import hackathon.server.dto.sign.TokenResponseDto;
import hackathon.server.dto.token.TokenDto;
import hackathon.server.entity.member.Authority;
import hackathon.server.entity.member.Member;
import hackathon.server.entity.member.RefreshToken;
import hackathon.server.exception.*;
import hackathon.server.repository.Member.MemberRepository;
import hackathon.server.repository.Member.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SignService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void userSignup(SignupRequestDto req) {
        validateSignUpInfo(req);

        Member member = Member.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .name(req.getName())
                .nickname(req.getNickname())
                .phone(req.getPhone())
                .address(req.getAddress())
                .authority(Authority.ROLE_USER)
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public void guideSignup(SignupRequestDto req) {
        validateSignUpInfo(req);

        Member member = Member.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .name(req.getName())
                .nickname(req.getNickname())
                .phone(req.getPhone())
                .address(req.getAddress())
                .authority(Authority.ROLE_GUIDE)
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public TokenResponseDto signIn(LoginRequestDto req) {
        Member member = memberRepository.findByUsername(req.getUsername()).orElseThrow(LoginFailureException::new);
        validatePassword(req, member);

        UsernamePasswordAuthenticationToken authenticationToken = req.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);

        TokenResponseDto tokenResponseDto = new TokenResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
        return tokenResponseDto;
    }

    @Transactional
    public TokenResponseDto reissue(ReissueRequestDto req) {
        if (!tokenProvider.validateToken(req.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(req.getAccessToken());
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        if (!refreshToken.getValue().equals(req.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        TokenResponseDto tokenResponseDto = new TokenResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
        return tokenResponseDto;
    }

    private void validateSignUpInfo(SignupRequestDto req) {
        if (memberRepository.existsByUsername(req.getUsername())) {
            throw new MemberUsernameAlreadyExistsException(req.getUsername());
        }
        if (memberRepository.existsByNickname(req.getNickname())) {
            throw new MemberNicknameAlreadyExistsException(req.getName());
        }
        if (memberRepository.existsByEmail(req.getEmail())) {
            throw new MemberEmailAlreadyExistsException(req.getEmail());
        }
        if (memberRepository.existsByPhone(req.getPhone())) {
            throw new MemberPhoneAlreadyExistsException(req.getPhone());
        }
    }

    private void validatePassword(LoginRequestDto loginRequestDto, Member member) {
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new LoginFailureException();
        }
    }
}

