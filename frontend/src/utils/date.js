export function toInputDate(date) {
  const offset = date.getTimezoneOffset() * 60000;
  return new Date(date.getTime() - offset).toISOString().slice(0, 16);
}

export function withSeconds(value) {
  return value.length === 16 ? `${value}:00` : value;
}

