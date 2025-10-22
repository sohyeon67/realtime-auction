import React, { useEffect, useState } from 'react';
import { getRemainingTime } from '../../../utils/time';
import { Typography } from '@mui/material';

function AuctionTimer({ endTime }) {
  // 초기값 설정
  const [timeLeft, setTimeLeft] = useState(() => getRemainingTime(endTime));

  useEffect(() => {

    const timer = setInterval(() => {
      // 매초 갱신
      const remaining = getRemainingTime(endTime);
      setTimeLeft(remaining);

      // 남은 시간이 없으면 타이머 종료
      if (
        remaining.days === 0 &&
        remaining.hours === 0 &&
        remaining.minutes === 0 &&
        remaining.seconds === 0
      ) {
        clearInterval(timer);
      }

    }, 1000);

    // useEffect 안에서 return으로 함수를 넘기면, 컴포넌트가 언마운트될 때 실행되는 정리함수로 동작(cleanup)
    return () => clearInterval(timer);
  }, [endTime]);

  if (timeLeft.expired) {
    return <Typography variant="h6">경매 종료</Typography>
  }

  const { days, hours, minutes, seconds } = timeLeft;

  return (
    <Typography variant="h6">
      {days > 0 && `${days}일 `}
      {hours > 0 && `${hours}시간 `}
      {minutes > 0 && `${minutes}분 `}
      {minutes > 0 ? (seconds === 0 ? "0초" : `${seconds}초`) : seconds > 0 && `${seconds}초`}
    </Typography>
  );
}

export default AuctionTimer;
