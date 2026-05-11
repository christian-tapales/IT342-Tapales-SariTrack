import { Outlet } from 'react-router-dom';
import bgImage from '../../assets/Typical_sari-sari_store.jpg';

const AuthLayout = () => {
  return (
    <div
      className="min-h-screen w-full flex items-center justify-center bg-cover bg-center bg-no-repeat relative bg-slate-900"
      style={{ backgroundImage: `url(${bgImage})` }}
    >
      {/* Dark Overlay ensures that even if the image is white/bright, the screen isn't just "white" */}
      <div className="absolute inset-0 bg-black/50 backdrop-blur-[2px]"></div>

      <div className="relative z-10 w-full max-w-md mx-4">
        <Outlet />
      </div>
    </div>
  );
};

export default AuthLayout;