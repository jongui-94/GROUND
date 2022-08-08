import "./App.css";
import LoginPage from "pages/LoginPage";
import KakaoRedirectHandler from "components/Login/OAuth/KakaoRedirectHandler";
import GoogleRedirectHandler from "components/Login/OAuth/GoogleRedirectHandler";
import FindIdPage from "pages/FindIdPage";
import WelcomePage from "pages/WelcomePage";
import RegisterPage from "pages/RegisterPage";

import { BrowserRouter, Routes, Route } from "react-router-dom";
import FeedPage from "pages/FeedPage";
import FollowFeed from "components/Feed/Follow/FollowFeed";
import LatestFeed from "components/Feed/Latest/LatestFeed";
import MobileNotiPage from "pages/MobileNotiPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/" element={<LoginPage />} />
        <Route
          path="/oauth/callback/google"
          element={<GoogleRedirectHandler />}
        />
        <Route
          path="/oauth/callback/kakao"
          element={<KakaoRedirectHandler />}
        />
        <Route path="/findid" element={<FindIdPage />} />
        <Route path="/welcome" element={<WelcomePage />} />
        <Route path="/feed" element={<FeedPage />}>
          <Route path="follow" element={<FollowFeed />} />
          <Route path="latest" element={<LatestFeed />} />
        </Route>
        <Route path="/notification" element={<MobileNotiPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
