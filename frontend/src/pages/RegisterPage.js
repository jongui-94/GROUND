import logo from "assets/images/underline_logo.png";

import Container from "@mui/material/Container";
import Grid from "@mui/material/Grid";

import BasicInfo from "components/Register/BasicInfo";
import OtherInfo from "components/Register/OtherInfo";

import "styles/Register/RegisterPage.scss";

import { useState, useEffect } from "react";
import { signUp } from "api/register";
import RegisterModal from "components/Register/RegisterModal";
import { useAuth } from "auth/AuthProvider";
import axios from "axios";

function RegisterPage() {
  const [next, setNext] = useState(false);
  const [basicInfo, setBasicInfo] = useState({});
  const [open, setOpen] = useState(false);
  const { systemLogin } = useAuth();

  // 다음 버튼 핸들러
  const goToOtherInfo = () => {
    if (!next) {
      setNext(!next);
    }
  };
  // state 변경 함수
  const changeBasicInfo = (newBasicInfo) => {
    setBasicInfo(newBasicInfo);
  };
  const changeOtherInfo = (newOtherInfo) => {
    sendRequest(newOtherInfo);
  };
  // 회원가입 요청
  const sendRequest = (newOtherInfo) => {
    let info = {};
    Object.assign(info, basicInfo, newOtherInfo);
    // 일반 회원가입
    if (!localStorage.getItem("ftoken")) {
      signUp(
        info,
        (res) => {
          if (res.data === true) {
            setOpen(true);
          }
        },
        (err) => {
          console.log(err);
        }
      );
    }
    // 소셜 회원가입
    else {
      const ftoken = localStorage.getItem("ftoken");     
      const instance = axios.create({
        baseURL: "http://i7d103.p.ssafy.io/rest",
        headers: {
          "Content-type": "application/json",
        },
      });
      instance.interceptors.request.use(function (config) {
        // 요청을 보내기 전에 토큰 값 갱신
        config.headers.Authorization = "Bearer " + ftoken;
        return config;
      });
      instance.put(`/user/modifyUser`, newOtherInfo).then((res) => {
        setOpen(true);
      })
    }
  };

  useEffect(() => {
    // 새로고침, 창닫기 alert
    const preventClose = (e) => {
      e.preventDefault();
      e.returnValue = "";
    };

    (() => {
      window.addEventListener("beforeunload", preventClose);
    })();

    return () => {
      window.removeEventListener("beforeunload", preventClose);
    };
  }, []);

  // 소셜 로그인으로 토큰이 이미 존재하면 닉네임 입력 페이지로 이동
  useEffect(() => {
    if (localStorage.getItem("ftoken")) {
      goToOtherInfo();
    }
  }, []);

  return (
    <Container maxWidth="xs">
      <Grid
        className="register-form"
        container
        direction="column"
        justifyContent="center"
        alignItems="center"
      >
        <Grid item>
          <Grid
            className="register-form__logo-box"
            container
            direction="column"
          >
            <div className="register-form__logo-wrapper">
              <img
                className="register-form__logo"
                src={logo}
                alt="underline_logo"
              />
            </div>
            <div className="register-form__register">회원가입</div>
          </Grid>
        </Grid>
        {!next && (
          <BasicInfo
            changeBasicInfo={changeBasicInfo}
            goToOtherInfo={goToOtherInfo}
          />
        )}
        {next && <OtherInfo sendRequest={sendRequest} />}
      </Grid>
      <RegisterModal open={open} setOpen={setOpen} />
    </Container>
  );
}

export default RegisterPage;
