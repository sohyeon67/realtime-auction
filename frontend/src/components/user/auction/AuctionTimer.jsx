import React, { useEffect, useState } from 'react';

function AuctionTimer({ endTime }) {

  // 남은 시간 계산 함수
  const calculateTimeLeft = () => {
    const diff = new Date(endTime) - new Date();
    if (diff <= 0) return { days: 0, hours: 0, minutes: 0, seconds: 0 };

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff / (1000 * 60 * 60)) % 24);
    const minutes = Math.floor((diff / (1000 * 60)) % 60);
    const seconds = Math.floor((diff / 1000) % 60);

    return { days, hours, minutes, seconds };
  };

  // 초기값 설정
  const [timeLeft, setTimeLeft] = useState(() => calculateTimeLeft());

  useEffect(() => {
    const timer = setInterval(() => {

      const remaining = calculateTimeLeft();

      // 남은 시간이 없으면 타이머 종료
      if (
        remaining.days === 0 &&
        remaining.hours === 0 &&
        remaining.minutes === 0 &&
        remaining.seconds === 0
      ) {
        clearInterval(timer);
      }

      setTimeLeft(remaining);
    }, 1000);

    return () => clearInterval(timer);
  }, [endTime]);

  const { days, hours, minutes, seconds } = timeLeft;

  return (
    <div>
      {days > 0 && `${days}일 `}
      {hours > 0 && `${hours}시간 `}
      {minutes > 0 && `${minutes}분 `}
      {minutes > 0 ? (seconds === 0 ? "0초" : `${seconds}초`) : seconds > 0 && `${seconds}초`}
    </div>
  );
}

export default AuctionTimer;
