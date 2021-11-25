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
import React from "react";

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

    const renderTheme = () => {
        if (mode === "light") {
            return (
                <>
                    <link
                        rel="stylesheet"
                        type="text/css"
                        href="https://cdnjs.cloudflare.com/ajax/libs/github-markdown-css/5.0.0/github-markdown-light.min.css"
                    />
                    <link
                        rel="stylesheet"
                        type="text/css"
                        href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.3.1/styles/base16/tomorrow.min.css"
                    />
                </>
            );
        } else {
            return (
                <>
                    <style
                        dangerouslySetInnerHTML={{
                            __html: ".markdown-body{background-color:inherit!important}.bytemd{color:inherit!important;border:inherit!important;background-color:#2f3033!important}.bytemd-toolbar{color:inherit!important;background-color:#313336!important}.bytemd-toolbar-icon:hover{background-color:#000000!important}.CodeMirror{color:inherit!important;background-color:inherit!important}.CodeMirror-activeline-background{background-color:#7f7f7f!important}.CodeMirror-selected{background:#646464!important}.CodeMirror-focused .CodeMirror-selected{background:#707070!important}.CodeMirror-line::selection,.CodeMirror-line>span::selection,.CodeMirror-line>span>span::selection{background:#707070!important}.CodeMirror-line::-moz-selection,.CodeMirror-line>span::-moz-selection,.CodeMirror-line>span>span::-moz-selection{background:#707070!important}.CodeMirror-cursor{border-left:1px solid #fff;border-right:none;width:0}",
                        }}
                    />
                    <link
                        rel="stylesheet"
                        type="text/css"
                        href="https://cdnjs.cloudflare.com/ajax/libs/github-markdown-css/5.0.0/github-markdown-dark.min.css"
                    />
                    <link
                        rel="stylesheet"
                        type="text/css"
                        href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.3.1/styles/base16/tomorrow-night.min.css"
                    />
                </>
            );
        }
    };

    return props.viewOnly ? (
        <>
            {renderTheme()}
            <Viewer value={props.value} plugins={plugins} />
        </>
    ) : (
        <>
            {renderTheme()}
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
