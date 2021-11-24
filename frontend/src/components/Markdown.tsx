import breaks from "@bytemd/plugin-breaks";
import footnotes from "@bytemd/plugin-footnotes";
import gemoji from "@bytemd/plugin-gemoji";
import gfm from "@bytemd/plugin-gfm";
import highlight from "@bytemd/plugin-highlight";
import math from "@bytemd/plugin-math";
import mermaid from "@bytemd/plugin-mermaid";
import * as gfm_locales from "@bytemd/plugin-gfm/lib/locales/zh_Hans.json";
import * as math_locales from "@bytemd/plugin-math/lib/locales/zh_Hans.json";
import * as mermaid_locales from "@bytemd/plugin-mermaid/lib/locales/zh_Hans.json";
import * as editor_locales from "bytemd/lib/locales/zh_Hans.json";
import { Editor, Viewer } from "@bytemd/react";
import React, { useEffect } from "react";

// Import css
import "bytemd/dist/index.min.css";
import "katex/dist/katex.min.css";
import { StandardCSSProperties } from "@mui/system/styleFunctionSx/StandardCssProperties";
import { Image } from "mdast";
import { useTheme } from "@mui/material/styles";

const plugins = [
    breaks(),
    footnotes(),
    gemoji(),
    gfm({ locale: gfm_locales }),
    highlight(),
    math({ locale: math_locales }),
    mermaid({ locale: mermaid_locales }),
];

interface MarkdownProps {
    value: string;
    onChange?: (newValue: string) => void;
    editorMode?: "split" | "tab" | "auto";
    viewOnly?: boolean;
    height?: StandardCSSProperties["height"];
    uploadImages?: (
        files: File[]
    ) => Promise<Pick<Image, "url" | "alt" | "title">[]>;
}

const Markdown: React.FC<MarkdownProps> = (props) => {
    const theme = useTheme();
    const mode = theme.palette.mode;

    useEffect(() => {
        if (mode === "light") {
            require("github-markdown-css/github-markdown-light.css");
            require("highlight.js/styles/tomorrow.css");
        }
        if (mode === "dark") {
            require("./dark-mode-markdown-patch.css");
            require("github-markdown-css/github-markdown-dark.css");
            require("highlight.js/styles/tomorrow-night.css");
        }
    }, [mode]);

    return props.viewOnly ? (
        <Viewer value={props.value} plugins={plugins} />
    ) : (
        <>
            <Editor
                value={props.value}
                onChange={props.onChange}
                plugins={plugins}
                mode={props.editorMode}
                locale={editor_locales}
                uploadImages={props.uploadImages}
            />
            {props.height && (
                <style
                    dangerouslySetInnerHTML={{
                        __html: `
                .bytemd {
                    height: ${props.height};
                }`,
                    }}
                />
            )}
        </>
    );
};

export default Markdown;
