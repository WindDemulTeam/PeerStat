import React from "react";
import FadeLoader from "react-spinners/FadeLoader";
import Modal from "react-modal";
import classes from "./Loader.module.css"

const Loader = ({loading}) => {
  return (
    <>
      <Modal className={classes.area}
        isOpen={loading}
        ariaHideApp={false}
      >
        <FadeLoader
          loading={loading}
          height={30}
          width={10}
          radius={10}
          margin={20}
        />
      </Modal>
    </>
    );
};

export default Loader;
