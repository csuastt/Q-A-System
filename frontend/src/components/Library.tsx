import React, { useEffect, useMemo, useState } from "react";
import TextField from '@mui/material/TextField';
import Stack from '@mui/material/Stack';
import Autocomplete from '@mui/material/Autocomplete';


const Library = () => {
    const [keywords, setKeywords] = useState<string>("");
    const [input, setInput] = useState<string>("");


    const handleSubmitSearch = () => {
        setKeywords(input);
    }



};

export default Library;
