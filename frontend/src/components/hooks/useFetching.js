import { useCallback, useEffect, useState } from "react"

export default function useFetching(callback, dependencies = []) {
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(false)

    const callbackMemoized = useCallback(async (...args) => {
        try {
            setLoading(true)
            await callback(...args)
        } catch (e) {
            setError(true);
        } finally {
            setLoading(false)
        }
        // eslint-disable-next-line
    }, dependencies)

    useEffect(() => {
        callbackMemoized()
    }, [callbackMemoized])

    return { loading, error }
}