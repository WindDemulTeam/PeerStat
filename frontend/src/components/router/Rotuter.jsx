import React from 'react';
import {Route, Routes, Navigate} from "react-router-dom";
import Map from '../map/Map';
import Stat from '../tables/Stat';
import Project from '../tables/Project';
import Availability from '../tables/Availability';

const Rotuter = () => {
    return (
        <Routes>
            <Route path="/map" exact element={ <Map/> } />
            <Route path="/stat" exact element={ <Stat/> } />
            <Route path="/project" exact element={ <Project/> } />
            <Route path="/availability" exact element={ <Availability/> } />
            <Route path="*" element={<Navigate to="/map" />} />
        </Routes>
    );
};

export default Rotuter;