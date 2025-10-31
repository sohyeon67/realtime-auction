export const getRemainingTime = (endTime) => {
  const diff = new Date(endTime) - new Date();
  if (diff <= 0) return { days: 0, hours: 0, minutes: 0, seconds: 0, expired: true };

  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((diff / (1000 * 60 * 60)) % 24);
  const minutes = Math.floor((diff / (1000 * 60)) % 60);
  const seconds = Math.floor((diff / 1000) % 60);

  return { days, hours, minutes, seconds, expired: false };
};

export const formatRemainingTime = (endTime) => {
  const { days, hours, minutes, expired } = getRemainingTime(endTime);

  if (expired) return "마감";

  if (days > 0) return `${days}일 ${hours}시간`;

  const pad = (n) => String(n).padStart(2, "0");
  return `${hours}시간 ${pad(minutes)}분`;
};