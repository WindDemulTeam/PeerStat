import { useState } from "react";

export const useFetching = (callback) => {
  const [loading, setLoading] = useState(false);

  const fetching = async (...args) => {
    setLoading(true);
    await callback(...args);
    setLoading(false);
  };

  return [fetching, loading];
};
